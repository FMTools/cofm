package collab.fm.mining.constraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;

import collab.fm.mining.TextData;
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
	
	private static final String[] sentenceEnds = {".", "!", "?"};
	
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
	
	private static Map<Long, TextData> featureText = new HashMap<Long, TextData>();
	private static Map<Long, TextData> featureNames = new HashMap<Long, TextData>();
	
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
	
	private int predictedClass;
	
	// ------ Persistence-oriented properties (Irrelevant to machine learning) -------
	private Long id;
	private Model model;
	private Entity first;
	private Entity second;
	private Relation constraint;  // Constraint between the first and second feature (if any).
	
	// ------ Attributes for machine learning ------
	// The Class label of the pair
	private int label;
	
	// Similarity of the descriptions/names of the two features
	private double firstAsObject;   // Probability of the first as second's object
	private double secondAsObject;
	private double objectSim; // Object similarity
	private double totalSim; // Whole text similarity
	
	public static void clearFeatureSet() {
		FeaturePair.featureText.clear();
		FeaturePair.featureNames.clear();
		TextData.resetDocumentVector();
	}
	
	public FeaturePair() {
		// Leave it for Hibernate framework.
	}
	
	// The pair always assumes the Entity is a Feature.
	public FeaturePair(Entity first, Entity second) {
		this.setFirst(first);
		this.setSecond(second);
		this.setModel(first.getModel());
		
		calcRelationAttributes(first, second);
		
		checkAndAddText(first);
		checkAndAddText(second);
	}
	
	public Pair<TextData, TextData> getPairText() {
		return Pair.make(featureText.get(first.getId()), 
				featureText.get(second.getId()));
	}
	
	public void updateTextSimilarity() {
		this.setTotalSim(TextSimilarity.byTfIdf(
				FeaturePair.featureText.get(first.getId()).getTermVector(), 
				FeaturePair.featureText.get(second.getId()).getTermVector(),
				TextData.getDocumentVector(),
				TextData.getNumDocument()));
		
		this.setFirstAsObject(TextSimilarity.byTfIdf(
				FeaturePair.featureNames.get(first.getId()).getTermVector(), 
				FeaturePair.featureText.get(second.getId()).getObjectTermVector(),
				TextData.getDocumentVector(),
				TextData.getNumDocument()));
		
		this.setSecondAsObject(TextSimilarity.byTfIdf(
				FeaturePair.featureNames.get(second.getId()).getTermVector(), 
				FeaturePair.featureText.get(first.getId()).getObjectTermVector(),
				TextData.getDocumentVector(),
				TextData.getNumDocument()));
		
		this.setObjectSim(TextSimilarity.byTfIdf(
				FeaturePair.featureText.get(first.getId()).getObjectTermVector(), 
				FeaturePair.featureText.get(second.getId()).getObjectTermVector(),
				TextData.getDocumentVector(),
				TextData.getNumDocument()));
	}
	
	private void checkAndAddText(Entity feature) {
		if (FeaturePair.featureText.get(feature.getId()) == null) {
			FeaturePair.featureText.put(feature.getId(), 
					new TextData(getDescriptions(feature), true));
		}
		if (FeaturePair.featureNames.get(feature.getId()) == null) {
			FeaturePair.featureNames.put(feature.getId(),
					new TextData(getNames(feature), false));
		}
	}
	
	private void calcRelationAttributes(Entity first, Entity second) {
		
		this.setLabel(NO_CONSTRAINT);
		this.setConstraint(null);

		for (Relation rel: first.getRels()) {
			if (!(rel instanceof BinRelation)) {
				continue;
			}
			BinRelation r = (BinRelation) rel;
			if (DataItemUtil.isBinRelationBetween(r, first, second)) {
				if (isRequire(r)) {
					if (this.getLabel() == NO_CONSTRAINT) {
						this.setLabel(REQUIRE);
						this.setConstraint(rel);
					}
				} else if (isExclude(r)) {
					if (this.getLabel() == NO_CONSTRAINT) {
						this.setLabel(EXCLUDE);
						this.setConstraint(rel);
					}
				} 
			} 
		}
	}
	
	
	private String getNames(Entity en) {
		// Combine all names (skip this @ 2011/10/4)
		StringBuilder name = new StringBuilder();
		List<Value> ns = en.getValuesByAttrName(Resources.ATTR_ENTITY_NAME);
		if (ns != null) {
			for (Value n: ns) {
				name.append(n.decodeQuotes() + " ");
			}
		}
		return name.toString().trim();
	}
	
	private String getDescriptions(Entity en) {
		StringBuilder des = new StringBuilder();
		// Combine all descriptions of "en" into a whole.
		List<Value> values = en.getValuesByAttrName(Resources.ATTR_ENTITY_DES);
		if (values != null) {
			for (Value v: values) {
				String s = v.decodeQuotes().trim();
				if (!ArrayUtils.contains(sentenceEnds, s.charAt(s.length()-1))) {
					s += sentenceEnds[0];
				}
				des.append(s + " ");
			}
		}
		return des.toString().trim();
	}
	
	public static boolean isRequire(Relation rel) {
		return ArrayUtils.contains(requireAlias, rel.getType().getTypeName());
	}
	
	public static boolean isExclude(Relation rel) {
		return ArrayUtils.contains(excludeAlias, rel.getType().getTypeName());
	}
	
	public static boolean isRefine(Relation rel) {
		return !isRequire(rel) && !isExclude(rel);
	}
	
	public String getPairInfo() {
		return "Feature #1: " + printFeature(first) + "Feature #2: " + printFeature(second) +
			"label=" + (this.getLabel() == EXCLUDE ?  "EXCLUDES" : (
					this.getLabel() == REQUIRE ? "REQUIRES" : "NO_CONSTRAINT")) + "\n" +
			"(total_sim=" + this.getTotalSim() +
			", object_sim=" + this.getObjectSim() +
			", 1asObject=" + this.getFirstAsObject() +
			", 2asObject=" + this.getSecondAsObject() + ")";
	}
	
	private String printFeature(Entity en) {
		// Format: 
		// Name 1 (Alias 1.1, Alias 1.2)
		//     Description 1.1
		//     Description 1.2
		String result = "";
		List<Value> name1 = en.getValuesByAttrName(Resources.ATTR_ENTITY_NAME);
		if (name1 == null) {
			result += "(Unnamed)";
		} else {
			Collections.sort(name1);
			Collections.reverse(name1);
			result += name1.get(0).decodeQuotes();
			if (name1.size() > 1) {
				result += " (Aliases: " + name1.get(1).decodeQuotes();
			}
			for (int i = 2; i < name1.size(); i++) {
				result += ", " + name1.get(i).decodeQuotes();
			}
			if (name1.size() > 1) {
				result += ")";
			}
		}
		result += "\n";
		List<Value> des = en.getValuesByAttrName(Resources.ATTR_ENTITY_DES);
		if (des != null) {
			for (Value d: des) {
				result += "\t" + d.decodeQuotes() + "\n";
			}
		}
		return result;
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

	public void setConstraint(Relation constraint) {
		this.constraint = constraint;
	}

	public Relation getConstraint() {
		return constraint;
	}

	public void setTotalSim(double totalSim) {
		this.totalSim = totalSim;
	}

	public double getTotalSim() {
		return totalSim;
	}

	public void setPredictedClass(int predictedClass) {
		this.predictedClass = predictedClass;
	}

	public int getPredictedClass() {
		return predictedClass;
	}

	public void setObjectSim(double objectSim) {
		this.objectSim = objectSim;
	}

	public double getObjectSim() {
		return objectSim;
	}

	public void setSecondAsObject(double secondAsObject) {
		this.secondAsObject = secondAsObject;
	}

	public double getSecondAsObject() {
		return secondAsObject;
	}

	public void setFirstAsObject(double firstAsObject) {
		this.firstAsObject = firstAsObject;
	}

	public double getFirstAsObject() {
		return firstAsObject;
	}
	
}
