package collab.test;

import org.junit.BeforeClass;
import org.junit.Test;

import collab.util.mock.MockServer;


public class MockIntegrationTest {
	
	protected static MockServer theServer;
	protected static final int testTimes = 100;
	
	@BeforeClass
	public static void setUp() {
		theServer = new MockServer();
	}
	
	@Test
	public void runServer() {
		theServer.run(testTimes);
	}
	
}
