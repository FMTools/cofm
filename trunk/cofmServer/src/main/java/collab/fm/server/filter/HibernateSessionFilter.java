package collab.fm.server.filter;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.persistence.HibernateUtil;
import collab.fm.server.util.exception.FilterException;

public class HibernateSessionFilter extends Filter {

	static Logger logger = Logger.getLogger(HibernateSessionFilter.class);
	
	private Session session;
	public HibernateSessionFilter() {
		
	}
	
	@Override
	protected boolean doBackwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		try {
			session.getTransaction().commit();
			logger.info("Transaction closed.");
			return true;
		} catch (HibernateException he) {
			logger.error("Couldn't commit transaction.", he);
			throw finalizeAndReport(he);
		}
	}

	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			logger.info("Transaction has began.");
			return true;
		} catch (HibernateException he) {
			logger.error("Couldn't begin transaction.", he);
			throw finalizeAndReport(he);
		}
	}
	
	private FilterException finalizeAndReport(HibernateException he) {
		try {
			session.getTransaction().rollback();
			return new FilterException(he);
		} catch (Exception rbe) {
			logger.warn("Couldn't rollback transaction.", rbe);
			return new FilterException(he);
		} finally {
			session.close();		
		}
	}
}
