package collab.test.data;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import collab.data.bean.*;
public class FeatureVoteTest {
	
	static Logger logger = Logger.getLogger(FeatureVoteTest.class);
	
	@Test
	public void testNormalVote() {
		Feature feat = new Feature();
		feat.setId(1);
		
		feat.voteFeature(true, 1);
		feat.voteFeature(false, 2);
		
		feat.voteName("rootFeat", true, 1);
		feat.voteName("root", true, 2);
		feat.voteName("rootFeat", true, 3);
		feat.voteName("rootFeat", false, 4);
		feat.voteName("root", false, 5);
		feat.voteName("root", true, 3);
		
		feat.voteChild(2, true, 1);
		feat.voteChild(3, true, 2);
		feat.voteChild(8, false, 3); // this vote should be treated as invalid
		
		feat.voteMandatory(false, 1);
		feat.voteMandatory(true, 3);
		
		logger.info(feat.toString());
	}
	
	@Test
	public void testRepeatedVoteBySameUser() {
		Feature feat = new Feature();
		feat.setId(2);
		
		// Repeated vote for Boolean 
		feat.voteFeature(true, 1);
		feat.voteFeature(false, 1);
		
		// Repeated vote for Mutex Group
		feat.voteName("root", true, 1);
		feat.voteName("root2", true, 1);
		
		// Repeated vote for Multi-Group
		feat.voteChild(3, true, 1);
		feat.voteChild(3, true, 1);
		feat.voteChild(3, false, 1);
		
		logger.info(feat.toString());
	}
	
	@Test
	public void testMultiThreadVote() {
		
	}
}
