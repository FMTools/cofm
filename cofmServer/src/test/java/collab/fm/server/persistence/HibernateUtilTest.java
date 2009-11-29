package collab.fm.server.persistence;

import org.apache.log4j.Logger;
import org.junit.*;
import static org.junit.Assert.*;

public class HibernateUtilTest {

	static Logger logger = Logger.getLogger(HibernateUtilTest.class);
	
	@Test 
	public void testGetSessionFactory() {
		HibernateUtil.getSessionFactory();
	}
}
