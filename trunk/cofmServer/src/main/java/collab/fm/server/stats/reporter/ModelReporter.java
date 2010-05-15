package collab.fm.server.stats.reporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.*;
import collab.fm.server.stats.StatsUtil;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;


/**
 * Report feature model details. (See TEMPLATEs.)
 * @author mark
 *
 */
public class ModelReporter implements Reporter {
	
	private static Logger logger = Logger.getLogger(ModelReporter.class);
	
	private static final String TEMPLATE_INTRO = "[Feature Model (ID = $id)]" + NL +
			"Model name: $name" + NL + 
			"=== Elements Overview ===" + NL +
			"Number of features: $nf" + NL +
			"Number of relationships: total $nrt refine $nrf require $nrq exclude $nre" + NL +
			"Number of feature names: total $fnt avg(#/feature) $fna lowest $fnlo highest $fnhi" + NL +
			"=== Contributions Overview ===" + NL +
			"Number of contributors: $nc" + NL +
			"Number of creations: total $ct avg(#/person) $ca lowest $clo highest $chi" + NL +
			"Number of YES votes: total $yt avg(#/person) $ya lowest $ylo highest $yhi" + NL +
			"Number of NO votes: total $nvt avg(#/person) $nva lowest $nvlo highest $nvhi" + NL +
			"=== Support/Oppose Overview ===" + NL +
			"Supporters of a person: avg $spa lowest $splo highest $sphi" + NL +
			"Opponents of a person: avg $opa lowest $oplo highest $ophi" + NL +
			"Supporters of a feature: avg $sfa lowest $sflo highest $sfhi" + NL +
			"Opponents of a feature: avg $ofa lowest $oflo highest $ofhi" + NL +
			"Supporters of a relationship: avg $sra lowest $srlo highest $srhi" + NL +
			"Opponents of a relationship: avg $ora lowest $orlo highest $orhi";
	
	private static final String TEMPLATE_MODEL_NAME = "$value ($yes:$no)";
	
	//TODO: format the feature details and relationship details, e.g.:
	//    FeatureID     Attribute     Value (&ID)   Creator  YES Voters     NO Voters
	//---------------------------------------------------------------------------------------
	//        1          /            /               1      (6)1,2,3,4,5,6  (2)7,8
	//        /         Name          feature XX(7)   1      (2)1,2          (6)3,4,5,6,7,8
	//        /         Name          feature YY(24)  4      (3)4,5,6        (5).....
	
	//TODO: use a properties file to define which models should be displayed.
	
	private String intro;
	private StringBuilder featureDetails = new StringBuilder();
	private StringBuilder relationDetails = new StringBuilder();
	
	public void report() {
		try {
			List<Model> models = DaoUtil.getModelDao().getAll();
			if (models != null) {
				for (Model m: models) {
					reportModel(m);
					logger.info(NL);
				}
			}
		} catch (BeanPersistenceException e) {
			logger.error("Reporter error.", e);
		} catch (StaleDataException e) {
			logger.error("Stale data error.", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	protected void reportModel(Model m) throws BeanPersistenceException, StaleDataException {
		String rslt = TEMPLATE_INTRO.replaceFirst("\\$id", m.getId().toString());
		// Model name
		Set<ModelName> names = (Set<ModelName>) m.getNames();
		StringBuilder strNames = new StringBuilder();
		for (ModelName name: names) {
			strNames.append(TEMPLATE_MODEL_NAME
					.replaceFirst("\\$value", name.getName())
					.replaceFirst("\\$yes", String.valueOf(name.getSupporterNum()))
					.replaceFirst("\\$no", String.valueOf(name.getOpponentNum()))
					+ "  ");
		}
		rslt = rslt.replaceFirst("\\$name", strNames.toString());
		
		// Contribution of Elements
		Map<Long, Contribution> coe = new HashMap<Long, Contribution>();
		
		// Elements Overview
		Counter fYes = new Counter(), fNo = new Counter();
		Counter rYes = new Counter(), rNo = new Counter();
		
		List<Feature> features = DaoUtil.getFeatureDao().getAll(m.getId());
		rslt = rslt.replaceFirst("\\$nf", StatsUtil.nullSafeSize(features));
		
		List<Relationship> relations = DaoUtil.getRelationshipDao().getAll(m.getId());
		rslt = rslt.replaceFirst("\\$nrt", StatsUtil.nullSafeSize(relations));
		List<BinaryRelationship> refines = null, requires = null, excludes = null;
		if (relations != null) {
			refines = new ArrayList<BinaryRelationship>();
			requires = new ArrayList<BinaryRelationship>();
			excludes = new ArrayList<BinaryRelationship>();
			for (Relationship r: relations) {
				
				addCon(coe, r);
				rYes.count(r.getSupporterNum());
				rNo.count(r.getOpponentNum());
				
				if (Resources.BIN_REL_REFINES.equals(r.getType())) {
					refines.add((BinaryRelationship) r);
				} else if (Resources.BIN_REL_REQUIRES.equals(r.getType())) {
					requires.add((BinaryRelationship) r);
				} else if (Resources.BIN_REL_EXCLUDES.equals(r.getType())) {
					excludes.add((BinaryRelationship) r);
				}
			}
		} 
		rslt = rslt.replaceFirst("\\$nrf", StatsUtil.nullSafeSize(refines))
			.replaceFirst("\\$nrq", StatsUtil.nullSafeSize(requires))
			.replaceFirst("\\$nre", StatsUtil.nullSafeSize(excludes));
		
		Counter fnameCounter = new Counter();
		if (features != null) {
			for (Feature f: features) {
				addCon(coe, f);
				
				fYes.count(f.getSupporterNum());
				fNo.count(f.getOpponentNum());
				fnameCounter.count(f.getNames().size());
			}
		}
		
		rslt = rslt.replaceFirst("\\$fnt", String.valueOf(fnameCounter.sum))
			.replaceFirst("\\$fna", fnameCounter.toAvg(StatsUtil.nullSafeIntSize(features)))
			.replaceFirst("\\$fnlo", String.valueOf(fnameCounter.min))
			.replaceFirst("\\$fnhi", String.valueOf(fnameCounter.max));
		
		//Contributions Overview
		List<User> users = DaoUtil.getUserDao().getAll(m.getId());
		rslt = rslt.replaceFirst("\\$nc", StatsUtil.nullSafeSize(users));
		
		// - feature names, descriptions, and optionality 
		if (features != null) {
			for (Feature f: features) {
				for (Votable v: f.getNames()) {
					FeatureName fn = (FeatureName) v;
					addCon(coe, fn);
				}
				for (Votable v: f.getDescriptions()) {
					FeatureDescription fd = (FeatureDescription) v;
					addCon(coe, fd);
				}
				for (Long u: f.getOptionality().getSupporters()) {
					addCon(coe, u, Contribution.YES_VOTE);
				}
				for (Long u: f.getOptionality().getOpponents()) {
					addCon(coe, u, Contribution.NO_VOTE);
				}
			}
		}
		
		Counter cc = new Counter(), yesc = new Counter(), noc = new Counter();
		Counter personYes = new Counter(), personNo = new Counter();
		for (Map.Entry<Long, Contribution> entry: coe.entrySet()) {
			cc.count(entry.getValue().creation);
			yesc.count(entry.getValue().yes);
			noc.count(entry.getValue().no);
			personYes.count(entry.getValue().supported.size());
			personNo.count(entry.getValue().opposed.size());
		}
		
		int uNum = StatsUtil.nullSafeIntSize(users);
		rslt = rslt.replaceFirst("\\$ct", String.valueOf(cc.sum))
			.replaceFirst("\\$ca", cc.toAvg(uNum))
			.replaceFirst("\\$clo", String.valueOf(cc.min))
			.replaceFirst("\\$chi", String.valueOf(cc.max))
			.replaceFirst("\\$yt", String.valueOf(yesc.sum))
			.replaceFirst("\\$ya", yesc.toAvg(uNum))
			.replaceFirst("\\$ylo", String.valueOf(yesc.min))
			.replaceFirst("\\$yhi", String.valueOf(yesc.max))
			.replaceFirst("\\$nvt", String.valueOf(noc.sum))
			.replaceFirst("\\$nva", noc.toAvg(uNum))
			.replaceFirst("\\$nvlo", String.valueOf(noc.min))
			.replaceFirst("\\$nvhi", String.valueOf(noc.max));
		
		//Support/Oppose Overview
		rslt = rslt.replaceFirst("\\$spa", personYes.toAvg(uNum))
			.replaceFirst("\\$splo", String.valueOf(personYes.min))
			.replaceFirst("\\$sphi", String.valueOf(personYes.max))
			.replaceFirst("\\$opa", personNo.toAvg(uNum))
			.replaceFirst("\\$oplo", String.valueOf(personNo.min))
			.replaceFirst("\\$ophi", String.valueOf(personNo.max))
			.replaceFirst("\\$sfa", fYes.toAvg(StatsUtil.nullSafeIntSize(features)))
			.replaceFirst("\\$sflo", String.valueOf(fYes.min))
			.replaceFirst("\\$sfhi", String.valueOf(fYes.max))
			.replaceFirst("\\$ofa", fNo.toAvg(StatsUtil.nullSafeIntSize(features)))
			.replaceFirst("\\$oflo", String.valueOf(fNo.min))
			.replaceFirst("\\$ofhi", String.valueOf(fNo.max))
			.replaceFirst("\\$sra", rYes.toAvg(StatsUtil.nullSafeIntSize(relations)))
			.replaceFirst("\\$srlo", String.valueOf(rYes.min))
			.replaceFirst("\\$srhi", String.valueOf(rYes.max))
			.replaceFirst("\\$ora", rNo.toAvg(StatsUtil.nullSafeIntSize(relations)))
			.replaceFirst("\\$orlo", String.valueOf(rNo.min))
			.replaceFirst("\\$orhi", String.valueOf(rNo.max));
		
		logger.info(rslt);
	}
	
	protected void addCon(Map<Long, Contribution> target, Votable element) {
		// Contribution of creation
		addCon(target, element.getCreator(), Contribution.CREATION);
		// Contribution of yes votes
		for (Long id: element.getVote().getSupporters()) {
			addCon(target, id, Contribution.YES_VOTE);
		}
		// Contribution of no votes
		for (Long id: element.getVote().getOpponents()) {
			addCon(target, id, Contribution.NO_VOTE);
		}
	}
	
	protected void addCon(Map<Long, Contribution> target, Long id, int type) {
		if (id < 0) {
			return;
		}
		Contribution con = target.get(id);
		boolean newItem = false;
		if (con == null) {
			newItem = true;
			con = new Contribution();
		}
		switch (type) {
			case Contribution.CREATION:
				con.creation++; break;
			case Contribution.YES_VOTE:
				con.yes++; 
				con.supported.add(id);
				break;
			case Contribution.NO_VOTE:
				con.no++; 
				con.opposed.add(id);
				break;
		}
		if (newItem) {
			target.put(id, con);
		}
	}
	
	protected static class Contribution {
		public static final int CREATION = 1;
		public static final int YES_VOTE = 2;
		public static final int NO_VOTE = 3;
		
		public int creation = 0;
		public int yes = 0;
		public int no = 0;
		
		public Set<Long> supported = new HashSet<Long>();
		public Set<Long> opposed = new HashSet<Long>();
	}
	
	protected static class Counter {
		public int min = 0;
		public int max = 0;
		public int sum = 0;
		
		private boolean hasMeetFirstNumber = false;
		
		public void count(int number) {
			sum += number;
			if (!hasMeetFirstNumber || number < min) {
				min = number;
			}
			if (!hasMeetFirstNumber || number > max) {
				max = number;
			}
			hasMeetFirstNumber = true;
		}
		
		// Output "sum / divider" (calculate average)
		public String toAvg(int divider) {
			return StatsUtil.zeroSafeAvg(sum, divider);
		}
	}

}
