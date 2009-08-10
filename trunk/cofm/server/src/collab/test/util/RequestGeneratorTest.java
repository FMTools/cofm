package collab.test.util;

import static org.junit.Assert.*;

import org.junit.Test;
import collab.util.mock.*;

public class RequestGeneratorTest {

	@Test
	public void testNextCommit() {
		for (int testCount = 5; testCount > 0; testCount--) {
			System.out.println(RequestGenerator.nextCommit());
		}
	}

}
