package collab.fm.mining.constraint;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import collab.fm.mining.TextSimilarity;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.Value;
import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.DataItemUtil;
import collab.fm.server.util.Pair;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

/**
 * The data item generated from a pair of features, used for constraints discovery.  
 * @author Li Yi
 *
 */
public class FeaturePair {
	
	static Logger logger = Logger.getLogger(FeaturePair.class);
	
	// The aliases of "require" relation used in the model. For example,
	// the "implemented-by" relation is a kind of "require" relation.
	// Client code can change this if needed.
	public static String[] requireAlias = {
		Resources.BIN_REL_REQUIRES
	};
	
	// The aliases of "exclude" relation used in the model.
	public static String[] excludeAlias = {
		Resources.BIN_REL_EXCLUDES
	};
	
	// The aliases of "refine" relation used in the model, e.g. "composed-by".
	public static String[] refineAlias = {
		Resources.BIN_REL_REFINES
	};
	
	// For boolean, note that in machine learning there is always a chance of an "Unknown" state 
	// so we use INT instead of BOOLEAN.
	public static final int YES = 1;
	public static final int NO = -1;
	public static final int UNKNOWN = 0;
	
	// Classes
	public static final int NO_CONSTRAINT = -1;
	public static final int REQUIRE = 1;
	public static final int EXCLUDE = 2;
	// public static final int UNKNOWN = 0;
	
	// ------ Persistence-oriented properties (Irrelevant to machine learning) -------
	private Long id;
	private Model model;
	private Entity first;
	private Entity second;
	
	public static int NUM_ATTRIBUTES = 8;
	// ------ Attributes for machine learning ------
	// The Class label of the pair
	private int label;
	
	// Similarity of the descriptions of the two features
	private float similarity;  
	
	// Do they have a parental relationship?
	private int parental;
	
	// Are they siblings?
	private int sibling;
	
	// How many mandatory features in this pair? (None = -1, Unknown = 0, One, Two)
	private int numMandatory;
	
	// Does the features require (required by) a feature who is not in this pair?
	private int requireOut;
	
	// Does the features exclude a feature who is not in this pair?
	private int excludeOut;
	
	// Does their parents require a outside-feature?
	private int parentRequireOut;
	
	// Does their parents exclude a outside-feature?
	private int parentExcludeOut;
	
	public FeaturePair() {
		// Leave it for Hibernate framework.
	}
	
	// The pair always assumes the Entity is a Feature.
	public FeaturePair(Entity first, Entity second) {
		this.setFirst(first);
		this.setSecond(second);
		this.setModel(first.getModel());
		
		calcRelationAttributes(first, second);
		
		this.setSimilarity(calcSimilarity(first, second));
		this.setNumMandatory(calcNumMan(first, second));
	}
	
	private void calcRelationAttributes(Entity first, Entity second) {
		// Relation-attributes including: Label, Parental, Sibling, RequireOut and ExcludeOut
		
		this.setLabel(NO_CONSTRAINT);
		this.setParental(NO);
		this.setSibling(UNKNOWN);
		
		int ro1 = NO, ro2 = NO, eo1 = NO, eo2 = NO;
		
		List<Long> parentsOfFirst = new ArrayList<Long>();
		List<Long> parentsOfSecond = new ArrayList<Long>();
		
		for (Relation rel: first.getRels()) {
			if (!(rel instanceof BinRelation)) {
				continue;
			}
			BinRelation r = (BinRelation) rel;
			if (DataItemUtil.isBinRelationBetween(r, first, second)) {
				if (isRequire(r)) {
					if (this.getLabel() == NO_CONSTRAINT) {
						this.setLabel(REQUIRE);
					}
				} else if (isExclude(r)) {
					if (this.getLabel() == NO_CONSTRAINT) {
						this.setLabel(EXCLUDE);
					}
				} else {
					this.setParental(YES);
					this.setSibling(NO);
				}
			} else {
				if (isRequire(r)) {
					ro1 = YES;
				} else if (isExclude(r)) {
					eo1 = YES;
				} else if (r.getTargetId().equals(first.getId())) {
					// If "first" is a child of another feature, record the feature.
					parentsOfFirst.add(r.getSourceId());
				}
			}
		}
		
		for (Relation rel: second.getRels()) {
			if (!(rel instanceof BinRelation)) {
				continue;
			}
			BinRelation r = (BinRelation) rel;
			if (!DataItemUtil.isBinRelationBetween(r, first, second)) {
				if (isRequire(r)) {
					ro2 = YES;
				} else if (isExclude(r)) {
					eo2 = YES;
				} else if (r.getTargetId().equals(second.getId())) {
					if ( this.getSibling() == UNKNOWN &&
							parentsOfFirst.contains(r.getSourceId())) {
						this.setSibling(YES);
						this.setParental(NO);
					}
					parentsOfSecond.add(r.getSourceId());
				}
			}
		}
		
		if (this.getSibling() == UNKNOWN) {
			this.setSibling(NO);
		}
		
		this.setRequireOut(sumThreeValueVars(ro1, ro2));
		this.setExcludeOut(sumThreeValueVars(eo1, eo2));
		
		Pair<Integer, Integer> po1 = calcPCO(parentsOfFirst, first, second);
		Pair<Integer, Integer> po2 = calcPCO(parentsOfSecond, first, second);
		this.setParentRequireOut(sumThreeValueVars(po1.first, po2.first));
		this.setParentExcludeOut(sumThreeValueVars(po1.second, po2.second));
	}
	
	private Pair<Integer, Integer> calcPCO(List<Long> parentIDs, Entity child1, Entity child2) {
		int pro = NO, peo = NO;
		logger.debug("Parent is " + parentIDs.toString());
		for (Long pid: parentIDs) {
			if (pro == YES && peo == YES) {
				break;
			}
			try {
				Entity p = DaoUtil.getEntityDao().getById(pid, false);
				if (p == null) {
					continue;
				}
				Pair<Integer, Integer> po = calcParentConstrainOutside(p, child1, child2);
				if (pro == NO) {
					pro = po.first;
				} 
				if (peo == NO) {
					peo = po.second;
				}
			} catch (ItemPersistenceException e) {
				logger.warn("Cannot read parent.", e);
				continue;
			} catch (StaleDataException e) {
				logger.warn("Cannot read parent.", e);
				continue;
			}
		}
		return Pair.make(pro, peo);
	}
	
	private Pair<Integer, Integer> calcParentConstrainOutside(Entity parent, Entity child1, Entity child2) {
		Pair<Integer, Integer> result = Pair.make(new Integer(NO), new Integer(NO));
		for (Relation r: parent.getRels()) {
			if (!(r instanceof BinRelation)) {
				continue;
			}
			BinRelation rel = (BinRelation) r;
			if (isRequire(rel) &&
				!rel.getTargetId().equals(child1.getId()) &&
				!rel.getTargetId().equals(child2.getId()) &&
				!rel.getSourceId().equals(child1.getId()) &&
				!rel.getSourceId().equals(child2.getId())) {
				result.first = YES;
			} else if (isExclude(rel)) {
				if (
				!rel.getTargetId().equals(child1.getId()) &&
				!rel.getTargetId().equals(child2.getId()) &&
				!rel.getSourceId().equals(child1.getId()) &&
				!rel.getSourceId().equals(child2.getId())) {
					result.second = YES;
				}
			}
		}
		return result;
	}
	
	private String getDescriptions(Entity en) {
		// Combine all names
		String des = "";
		List<Value> ns = en.getValuesByAttrName(Resources.ATTR_ENTITY_NAME);
		if (ns != null) {
			for (Value n: ns) {
				des += n.getVal() + " ";
			}
		}
		// Combine all descriptions of "en" into a whole.
		List<Value> values = en.getValuesByAttrName(Resources.ATTR_ENTITY_DES);
		if (values != null) {
			for (Value v: values) {
				des += v.getVal() + " ";
			}
		}
		return des;
	}
	
	private float calcSimilarity(Entity first, Entity second) {
		return TextSimilarity.bySimpleTf(getDescriptions(first), getDescriptions(second));
	}
		
	// Sum 2 Three-value variables (i.e., unknown/no/one values)
	private int sumThreeValueVars(int var1, int var2) {
		switch (var1) {
		case UNKNOWN:
			if (var2 == 1) {
				return 1;   // At least 1.
			}
			return UNKNOWN;
		case 1:
			if (var2 == 1) {
				return 2;  
			}
			return 1;   // At least 1.
		case NO:
			if (var2 == 1) {
				return 1;
			}
			if (var2 == NO) {
				return NO;
			}
			return UNKNOWN;
		}
		return UNKNOWN;
	}
	
	private int calcNumMan(Entity first, Entity second) {
		int m1 = isMandatory(first), m2 = isMandatory(second);
		return sumThreeValueVars(m1, m2);
	}
	
	private int isMandatory(Entity en) {
		List<Value> values = en.getValuesByAttrName(Resources.ATTR_FEATURE_OPT);
		if (values != null) {
			Float manSupport = 0.0f, optSupport = 0.0f;
			for (Value v: values) {
				if (Resources.VAL_OPT_MANDATORY.equals(v.getVal())) {
					manSupport = v.getSupportRate();
				} else if (Resources.VAL_OPT_OPTIONAL.equals(v.getVal())) {
					optSupport = v.getSupportRate();
				}
			}
			int compare = manSupport.compareTo(optSupport);
			if (compare < 0) {
				return NO; 
			} 
			if (compare > 0) {
				return YES;
			}
		}
		return UNKNOWN;
	}
		
	private boolean isRequire(Relation rel) {
		return ArrayUtils.contains(requireAlias, rel.getType().getTypeName());
	}
	
	private boolean isExclude(Relation rel) {
		return ArrayUtils.contains(excludeAlias, rel.getType().getTypeName());
	}
	
	// ------- Setters and Getters ------- 
	
	public Long getId() {
		return id;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Entity getFirst() {
		return first;
	}

	public void setFirst(Entity first) {
		this.first = first;
	}

	public Entity getSecond() {
		return second;
	}

	public void setSecond(Entity second) {
		this.second = second;
	}

	public float getSimilarity() {
		return similarity;
	}

	public void setSimilarity(float similarity) {
		this.similarity = similarity;
	}

	public int getParental() {
		return parental;
	}

	public void setParental(int parental) {
		this.parental = parental;
	}

	public int getSibling() {
		return sibling;
	}

	public void setSibling(int sibling) {
		this.sibling = sibling;
	}

	public int getNumMandatory() {
		return numMandatory;
	}

	public void setNumMandatory(int numMandatory) {
		this.numMandatory = numMandatory;
	}

	public int getRequireOut() {
		return requireOut;
	}

	public void setRequireOut(int requireOut) {
		this.requireOut = requireOut;
	}

	public int getExcludeOut() {
		return excludeOut;
	}

	public void setExcludeOut(int excludeOut) {
		this.excludeOut = excludeOut;
	}

	public void setParentExcludeOut(int parentExcludeOut) {
		this.parentExcludeOut = parentExcludeOut;
	}

	public int getParentExcludeOut() {
		return parentExcludeOut;
	}

	public void setParentRequireOut(int parentRequireOut) {
		this.parentRequireOut = parentRequireOut;
	}

	public int getParentRequireOut() {
		return parentRequireOut;
	}
	
	
}
