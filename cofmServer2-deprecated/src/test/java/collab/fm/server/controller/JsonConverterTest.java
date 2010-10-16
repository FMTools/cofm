package collab.fm.server.controller;

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.junit.*;

import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.bean.protocol.op.VoteAddFeatureRequest;
import collab.fm.server.bean.protocol.op.VoteAddFeatureRequest.DefaultResponse;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.JsonConvertException;

import static org.junit.Assert.*;

public class JsonConverterTest {
	
	private static Logger logger = Logger.getLogger(JsonConverterTest.class);
	
	@Test
	public void testVoteAddFeatureJson() throws JsonConvertException {
		String inputAdd = "{id:1,name:\"" + Resources.REQ_VA_FEATURE + 
			"\",requesterId:2,featureName:\"New Feature\"}";
		VoteAddFeatureRequest r1 = (VoteAddFeatureRequest) JsonConverter.jsonToRequest(inputAdd);
		DefaultResponse dr1 = new DefaultResponse(r1);
		dr1.setExist(new Boolean(false));
		
		String outputAdd = JsonConverter.responseToJson(dr1);
		System.out.println("----- " + outputAdd);
		
		String inputVote = "{id:1,name:\"" + Resources.REQ_VA_FEATURE + 
			"\",requesterId:2,featureId:3,yes:true}";
		VoteAddFeatureRequest r2 = (VoteAddFeatureRequest) JsonConverter.jsonToRequest(inputVote);
		DefaultResponse dr2 = new DefaultResponse(r2);
		dr2.setName("ORIGIN");
		dr2.setExist(new Boolean(true));
		dr2.setInferVotes(Arrays.asList(new Long[]{10L, 20L, 30L}));
		
		String outputVote = JsonConverter.responseToJson(dr2);
		System.out.println("----- " + outputVote);
		
		DefaultResponse dr22 = (DefaultResponse) dr2.clone();
		dr22.setName("CLONE");
		String outputVote2 = JsonConverter.responseToJson(dr22);
		System.out.println("----- " + outputVote2);
	}
	
	@Test
	public void testAddEnumAttributeRequestFromJson() {
		
	}
}
