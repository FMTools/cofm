package collab.fm.server.stats.reporter;

import java.util.*;

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
	
	private static final String USER_STATS_TITLE = String.format(
			"%-22s %-20s %-20s %n" +
			"%-12s %-4s %-4s %-6s %-6s %-6s %-6s %-6s %-6s %n" +
			"%-54s",
			" ", "Support(Create)", "Support(Vote)",
			"User Name", "C#", "V#", "avg", "min", "max", "avg", "min", "max",
			"---------------------------------------------------------------------------");
	
	private static final String TEMPLATE_INTRO = "[Feature Model (ID = $id)]" + NL +
			"Model name: $name" + NL + 
			"=== Elements Overview ===" + NL +
			"Number of features: $nf" + NL +
			"Number of relationships: total $nrt refine $nrf require $nrq exclude $nre" + NL +
			"Number of feature names: total $fnt avg(#/feature) $fna lowest $fnlo highest $fnhi" + NL +
			"Number of feature descriptions: total $fdt avg(#/feature) $fda lowest $fdlo highest $fdhi" + NL +
			"=== Contributions Overview ===" + NL +
			"Number of contributors: $nc" + NL +
			"Number of creations: total $ct avg(#/person) $ca lowest $clo highest $chi" + NL +
			"Number of YES votes: total $yt avg(#/person) $ya lowest $ylo highest $yhi" + NL +
			"Number of NO votes: total $nvt avg(#/person) $nva lowest $nvlo highest $nvhi" + NL +
			"=== Support/Oppose Overview ===" + NL +
			"Supporters of a feature: avg $sfa lowest $sflo highest $sfhi" + NL +
			"Opponents of a feature: avg $ofa lowest $oflo highest $ofhi" + NL +
			"Supporters of a relationship: avg $sra lowest $srlo highest $srhi" + NL +
			"Opponents of a relationship: avg $ora lowest $orlo highest $orhi" + NL;
	
	private static final String TEMPLATE_MODEL_NAME = "$value ($yes:$no)";
	
	// Template for user stats, arguments:
	// userName creation# vote# Supporting_Rate_of_creation(low high avg) Supporting_rate_of_voting(l h avg)
	private static final String TEMPLATE_USER_STATS = 
		"%-12s %-4d %-4d %-6.2f %-6.2f %-6.2f %-6.2f %-6.2f %-6.2f";
	
	
	// Template for supporting rate (Person)
	//   UserName     Creation#   Vote#  Supporting Rate of Creation(low high avg)   Supporting Rate of Voting(low high avg)
	// (Element)
	//   Supporting Rate         Count(ratio%)
	//
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
		
		// User Stats
		Map<Long, UserStats> userStatsMap = new HashMap<Long, UserStats>();
		
		// Elements Overview
		IntCounter fYes = new IntCounter(), fNo = new IntCounter();
		IntCounter rYes = new IntCounter(), rNo = new IntCounter();
		
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
				
				addUserStats(userStatsMap, r);
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
		
		IntCounter fnameCounter = new IntCounter(), fdCounter = new IntCounter();
		if (features != null) {
			for (Feature f: features) {
				addUserStats(userStatsMap, f);
				
				fYes.count(f.getSupporterNum());
				fNo.count(f.getOpponentNum());
				fnameCounter.count(f.getNames().size());
				fdCounter.count(f.getDescriptions().size());
			}
		}
		
		rslt = rslt.replaceFirst("\\$fnt", String.valueOf(fnameCounter.sum))
			.replaceFirst("\\$fna", fnameCounter.toAvg(StatsUtil.nullSafeIntSize(features)))
			.replaceFirst("\\$fnlo", String.valueOf(fnameCounter.min))
			.replaceFirst("\\$fnhi", String.valueOf(fnameCounter.max))
			.replaceFirst("\\$fdt", String.valueOf(fdCounter.sum))
			.replaceFirst("\\$fda", fdCounter.toAvg(StatsUtil.nullSafeIntSize(features)))
			.replaceFirst("\\$fdlo", String.valueOf(fdCounter.min))
			.replaceFirst("\\$fdhi", String.valueOf(fdCounter.max));
		
		//Contributions Overview
		List<User> users = DaoUtil.getUserDao().getAll(m.getId());
		rslt = rslt.replaceFirst("\\$nc", StatsUtil.nullSafeSize(users));
		
		// - feature names, descriptions, and optionality 
		if (features != null) {
			for (Feature f: features) {
				for (Votable v: f.getNames()) {
					FeatureName fn = (FeatureName) v;
					addUserStats(userStatsMap, fn);
				}
				for (Votable v: f.getDescriptions()) {
					FeatureDescription fd = (FeatureDescription) v;
					addUserStats(userStatsMap, fd);
				}
				OptionalityAdapter oa = new OptionalityAdapter(f.getOptionality());
				addUserStats(userStatsMap, oa);
			}
		}
		
		IntCounter cc = new IntCounter(), yesc = new IntCounter(), noc = new IntCounter();
		for (Map.Entry<Long, UserStats> entry: userStatsMap.entrySet()) {
			cc.count(entry.getValue().creation);
			yesc.count(entry.getValue().yes);
			noc.count(entry.getValue().no);
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
		rslt = rslt
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
		
		Map<Long, String> us = new HashMap<Long, String>();
		for (User u: users) {
			us.put(u.getId(), u.getName());
		}
		reportUserStats(userStatsMap, us);
	}
	
	protected void reportUserStats(Map<Long, UserStats> map, Map<Long, String> users) {
		StringBuilder sb = new StringBuilder();
		sb.append("=== User contribution/rating details===" + NL + USER_STATS_TITLE + NL);
		
		// 1. Sort the map (Reverse)
		List<Map.Entry<Long, UserStats>> list = new LinkedList<Map.Entry<Long, UserStats>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Long, UserStats>>(){
			public int compare(Map.Entry<Long, UserStats> o1, Map.Entry<Long, UserStats> o2) {
				return -(o1.getValue().compareTo(o2.getValue()));
			}
		});
		
		// 2. Report by order
		for (Map.Entry<Long, UserStats> e: list) {
			e.getValue().calculateSupportRate();
			sb.append(String.format(TEMPLATE_USER_STATS, 
					safeGetName(users, e.getKey()),
					e.getValue().creation,
					e.getValue().yes + e.getValue().no,
					e.getValue().avgCreationSupport,
					e.getValue().minCreationSupport,
					e.getValue().maxCreationSupport,
					e.getValue().avgVoteSupport,
					e.getValue().minVoteSupport,
					e.getValue().maxVoteSupport
					));
			sb.append(NL);
		}
	}
	
	protected String safeGetName(Map<Long, String> users, Long id) {
		String name = users.get(id);
		if (name == null) {
			return "User# " + id.toString();
		}
		return name;
	}
	
	protected UserStats safeGet(Map<Long, UserStats> map, Long id) {
		UserStats c = map.get(id);
		if (c == null) {
			c = new UserStats();
			map.put(id, c);
		}
		return c;
	}
	
	protected void addUserStats(Map<Long, UserStats> target, Votable element) {
		addCreationStats(target, element);
		addVoteStats(target, element);
	}
	
	protected void addVoteStats(Map<Long, UserStats> target, Votable element) {
		// Add number of voting, and support info to every voter of this Element
		// Yes Voters
		for (Long id: element.getVote().getSupporters()) {
			Support vs = new Support();
			UserStats c = safeGet(target, id);
			c.yes++;
			vs.yes = element.getSupporterNum();
			vs.no = element.getOpponentNum();
			c.voteSupport.add(vs);
		}
		// No Voters
		for (Long id: element.getVote().getOpponents()) {
			Support vs = new Support();
			UserStats c = safeGet(target, id);
			c.no++;
			vs.yes = element.getOpponentNum();
			vs.no = element.getSupporterNum();
			c.voteSupport.add(vs);
		}
	}
	
	protected void addCreationStats(Map<Long, UserStats> target, Votable element) {
		if (!element.hasCreator()) {
			return;
		}
		Support cs = new Support();
		UserStats con = safeGet(target, element.getCreator());
		
		con.creation++;
		cs.yes = element.getSupporterNum();
		cs.no = element.getOpponentNum();
		con.creationSupport.add(cs);
	}
	
	protected static class UserStats implements Comparable<UserStats>{
		public int creation = 0;
		public int yes = 0;
		public int no = 0;
		
		public List<Support> creationSupport = new ArrayList<Support>();
		public List<Support> voteSupport = new ArrayList<Support>();
		
		public float avgCreationSupport;
		public float minCreationSupport;
		public float maxCreationSupport;
		
		public float avgVoteSupport;
		public float minVoteSupport;
		public float maxVoteSupport;
		
		public void calculateSupportRate() {
			SupportCounter csc = calculateRate(creationSupport);
			avgCreationSupport = csc.toAvg();
			minCreationSupport = csc.min;
			maxCreationSupport = csc.max;
			
			SupportCounter vsc = calculateRate(voteSupport);
			avgVoteSupport = vsc.toAvg();
			minVoteSupport = vsc.min;
			maxVoteSupport = vsc.max;
		}
		
		private SupportCounter calculateRate(List<Support> ss) {
			SupportCounter c = new SupportCounter();
			for (Support s: ss) {
				c.count(s);
			}
			return c;
		}

		public int compareTo(UserStats o) {
			// sort by creation number first, vote number second
			if (this.creation < o.creation) {
				return -1;
			}
			if (this.creation > o.creation) {
				return 1;
			}
			int v = this.yes + this.no - o.yes - o.no;
			if (v < 0) {
				return -1;
			}
			if (v > 0) {
				return 1;	
			}
			return 0;
		}
	}
	
	protected static class Support {
		public int yes = 0;
		public int no = 0;
		public float rate() {
			if (no == 0) {
				return 100.0f;
			}
			return (float) (100.0 * yes / (yes + no));
		}
	}
	
	protected static class SupportCounter {
		public float min = 0.0f;
		public float max = 100.0f;
		public int yes = 0;
		public int no = 0;
		
		private boolean hasMeetFirst = false;
		
		public void count(Support s) {
			yes += s.yes;
			no += s.no;
			float rate = s.rate();
			if (!hasMeetFirst || rate < min) {
				min = rate;
			}
			if (!hasMeetFirst || rate > max) {
				max = rate;
			}
			hasMeetFirst = true;
		}
		
		public float toAvg() {
			if (no == 0) {
				return 100.0f;
			}
			return (float) (100.0 * yes / (yes + no));
		}
	}
	
	protected static class IntCounter {
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
	
	protected static class OptionalityAdapter implements Votable {
		
		private Vote opt;
		
		public OptionalityAdapter(Vote opt) {
			this.opt = opt;
		}
		
		public Long getCreator() {
			return Votable.VOID_CREATOR;
		}

		public int getOpponentNum() {
			return opt.getOpponents().size();
		}

		public int getSupporterNum() {
			return opt.getSupporters().size();
		}

		public Vote getVote() {
			return opt;
		}

		public boolean hasCreator() {
			return false;
		}

		public void vote(boolean yes, Long userid) {
			opt.vote(yes, userid);
		}
		
	}

}
