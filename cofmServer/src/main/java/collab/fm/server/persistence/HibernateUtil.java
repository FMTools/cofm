package collab.fm.server.persistence;

import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.cfg.*;

public class HibernateUtil {
	
	static Logger logger = Logger.getLogger(HibernateUtil.class);
	
	private static SessionFactory sessionFactory;
	static {
		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();
			logger.debug("SessionFactory has built.");
		} catch (Throwable e) {
			logger.error("Can't build session factory.", e);
			throw new ExceptionInInitializerError(e);
		}
	}
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public static Session getCurrentSession() {
		return getSessionFactory().getCurrentSession();
	}
	
	public static void shutdown() {
		getSessionFactory().close();
	}
}
