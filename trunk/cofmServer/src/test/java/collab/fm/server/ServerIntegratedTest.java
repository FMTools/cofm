package collab.fm.server;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import collab.fm.server.bean.entity.*;
import collab.fm.server.bean.operation.*;
import collab.fm.server.bean.protocol.*;
import collab.fm.server.controller.*;
import collab.fm.server.util.exception.*;
import collab.fm.server.util.*;
@Ignore
public class ServerIntegratedTest {
	
	static Logger logger = Logger.getLogger(ServerIntegratedTest.class);
	
	private static final String IP1 = "123.123.123.1:12345";
	private static final String IP2 = "123.123.123.2:54321";
	
	// (The ID of) two users.
	private static int user1;
	private static int user2;
	
	@BeforeClass
	public static void init() {
		Controller.init();
		
		// Register and login.
		user1 = registerAndLogin("Admin", "123", IP1);
		user2 = registerAndLogin("God", "hoho", IP2);
	}
	
	private static int registerAndLogin(String name, String pwd, String ip) {
		Controller.instance().execute(RequestGenerator.register(name, pwd), ip);
		
		ResponseGroup rg = Controller.instance()
			.execute(RequestGenerator.login(name, pwd), ip);
		return rg.getBack().getRequesterId().intValue();
	}
	
	@Test
	public void testUnauthorizedRequest() {
		// User 1 uses a different IP (IP2) to send request.
		RequestGenerator.userId = user1;
		
		Controller.instance()
			.execute(RequestGenerator.createModel("Music Player", "This should fail."), IP2);
	}
	
	@Test
	public void testCreateFeaturesAndRelationships() {
		
		// User 1 creates the model
		RequestGenerator.userId = user1;
		ResponseGroup rg = Controller.instance()
			.execute(RequestGenerator.createModel("Video Player", "Video playing software"), IP1);
		RequestGenerator.modelId = ((CreateModelResponse)rg.getBack()).getModelId().intValue();
		
		// User 1 creates two features
		rg = Controller.instance()
			.execute(RequestGenerator.createFeature("Play Control"), IP1);
		FeatureOperation fo1 = (FeatureOperation) ((CommitResponse)rg.getBack()).getOperations().get(0);
		int f1 = fo1.getFeatureId().intValue();
		
		rg = Controller.instance()
			.execute(RequestGenerator.createFeature("Basic Control"), IP1);
		FeatureOperation fo2 = (FeatureOperation) ((CommitResponse)rg.getBack()).getOperations().get(0);
		int f2 = fo2.getFeatureId().intValue();
		
		// User 2 creates a refinement relationship between these two features
		RequestGenerator.userId = user2;
		rg = Controller.instance().execute(
				RequestGenerator.createBinaryRelationship(Resources.BIN_REL_REFINES, 
						f1, f2), IP2);
		RelationshipOperation ro = (RelationshipOperation) 
			((CommitResponse)rg.getBack()).getOperations().get(0);
		int r = ro.getRelationshipId().intValue();
		
	}
}
