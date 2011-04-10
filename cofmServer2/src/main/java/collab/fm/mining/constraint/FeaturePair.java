package collab.fm.mining.constraint;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import collab.fm.mining.TextSimilarity;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.Value;
import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.util.DataItemUtil;
import collab.fm.server.util.Resources;

/**
 * The data item generated from a pair of features, used for constraints discovery.  
 * @author Li Yi
 *
 */
public class FeaturePair {
	
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
	
	public static int NUM_ATTRIBUTES = 6;
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
	
	// Does one of the features require (required by) a feature who is not in this pair?
	private int requireOut;
	
	// Does one of the features exclude a feature who is not in this pair?
	private int excludeOut;
	
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
		this.setRequireOut(NO);
		this.setExcludeOut(NO);
		
		List<Long> parentsOfFirst = new ArrayList<Long>();
		
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
				} else if (isRefine(r)) {
					this.setParental(YES);
					this.setSibling(NO);
				}
			} else {
				if (isRequire(r)) {
					this.setRequireOut(YES);
				} else if (isExclude(r)) {
					this.setExcludeOut(YES);
				} else if (isRefine(r) && r.getTargetId().equals(first.getId())) {
					// If "first" is a child of another feature, record the feature.
					parentsOfFirst.add(r.getSourceId());
				}
			}
		}
		
		for (Relation rel: second.getRels()) {
			if (!(rel instanceof BinRelation)) {
				continue;
			}
			if (this.getRequireOut() == YES && this.getExcludeOut() == YES &&
					this.getSibling() != UNKNOWN) {
				break;
			}
			BinRelation r = (BinRelation) rel;
			if (!DataItemUtil.isBinRelationBetween(r, first, second)) {
				if (isRequire(r)) {
					this.setRequireOut(YES);
				} else if (isExclude(r)) {
					this.setExcludeOut(YES);
				} else if (isRefine(r) && this.getSibling() == UNKNOWN && 
						r.getTargetId().equals(second.getId())) {
					if (parentsOfFirst.contains(r.getSourceId())) {
						this.setSibling(YES);
					}
				}
			}
		}
		
		if (this.getSibling() == UNKNOWN) {
			this.setSibling(NO);
		}
	}
	
	private String getDescriptions(Entity en) {
		// Combine all descriptions of "en" into a whole.
		String des = "";
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
		
	private int calcNumMan(Entity first, Entity second) {
		int m1 = isMandatory(first), m2 = isMandatory(second);
		switch (m1) {
		case UNKNOWN:
			if (m2 == YES) {
				return 1;   // At least 1 mandatory.
			}
			return UNKNOWN;
		case YES:
			if (m2 == YES) {
				return 2;   // 2 mandatory features
			}
			return 1;   // At least 1.
		case NO:
			if (m2 == YES) {
				return 1;
			}
			if (m2 == NO) {
				return NO;
			}
			return UNKNOWN;
		}
		return UNKNOWN;
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
	
	private boolean isRefine(Relation rel) {
		return ArrayUtils.contains(refineAlias, rel.getType().getTypeName());
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
	
	
}
