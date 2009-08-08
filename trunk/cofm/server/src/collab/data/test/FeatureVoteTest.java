package collab.data.test;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;

import collab.data.bean.*;
public class FeatureVoteTest {
	
	static Logger logger = Logger.getLogger(FeatureVoteTest.class);
	
	@Test
	public void testNormalVote() {
		logger.info("test normal");
	}
	
	@Test
	public void testRepeatedVoteBySameUser() {
		
	}
	
	@Test
	public void testVoteForInexistentValue() {
		
	}
	
	@Test
	public void testMultiThreadVote() {
		
	}
}
