package collab.fm.server.filter;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.persistence.HibernateUtil;
import collab.fm.server.util.exception.ItemPersistenceException;

public class HibernateSessionFilter extends Filter {

	static Logger logger = Logger.getLogger(HibernateSessionFilter.class);
	
	private Session session;
	public HibernateSessionFilter() {
		
	}
	
	@Override
	protected boolean doBackwardFilter(Request req, ResponseGroup rg)
		throws ItemPersistenceException {
		try {
			session.getTransaction().commit();
			logger.info("Transaction closed.");
			return true;
		} catch (HibernateException he) {
			logger.error("Couldn't commit transaction.", he);
			ItemPersistenceException e = finalizeAndReport(he);
			throw e;
		}
	}

	@Override
	protected boolean doForwardFilter(Request req, ResponseGroup rg)
			throws ItemPersistenceException {
		try {
			session = HibernateUtil.getSessionFactory().getCurrentSession();
			session.beginTransaction();
			logger.info("Transaction has began.");
			return true;
		} catch (HibernateException he) {
			logger.error("Couldn't begin transaction.", he);
			ItemPersistenceException e = finalizeAndReport(he);
			throw e;
		}
	}
	
	private ItemPersistenceException finalizeAndReport(HibernateException he) {
		try {
			session.getTransaction().rollback();
			return new ItemPersistenceException(he);
		} catch (Exception rbe) {
			logger.warn("Couldn't rollback transaction.", rbe);
			return new ItemPersistenceException(he);
		} finally {
			session.close();		
		}
	}

	@Override
	protected void doDisconnection(Integer client, ResponseGroup rg) {
		// TODO Auto-generated method stub
		
	}

}
