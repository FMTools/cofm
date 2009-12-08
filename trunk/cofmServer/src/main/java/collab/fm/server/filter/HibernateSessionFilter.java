package collab.fm.server.filter;

import org.apache.log4j.Logger;
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
			session.close();
			return true;
		} catch (Exception e) {
			logger.error("Couldn't commit transaction.", e);
			req.setLastError("Couldn't commit transaction.");
			throw new FilterException("Transaction commit error.", e);
		}
	}

	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg)
			throws FilterException {
		session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		return true;
	}
	
	@Override
	protected FilterException onError(Request req, ResponseGroup rg, Exception e) {
		try {
			session.getTransaction().rollback();
			return new FilterException(e);
		} catch (Exception rbe) {
			logger.error("Couldn't rollback transaction.", rbe);
			return new FilterException(e);
		} finally {
			session.close();		
		}
	}
}
