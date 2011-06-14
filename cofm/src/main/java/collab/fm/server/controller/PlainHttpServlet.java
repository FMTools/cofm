package collab.fm.server.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import collab.fm.server.bean.persist.User;
import collab.fm.server.persistence.HibernateUtil;
import collab.fm.server.util.*;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class PlainHttpServlet extends HttpServlet {

	static Logger logger = Logger.getLogger(PlainHttpServlet.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1861041724700087840L;

	private static final String ACTION_VERIFICATION = "vf";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		String action = req.getParameter("a");
		if (action .equals(ACTION_VERIFICATION)) {
			handleVerification(req, res);
		}
		
	}

	private void handleVerification(HttpServletRequest req,
			HttpServletResponse res) throws ServletException, IOException {
		String name = req.getParameter("n");
		String validStr = req.getParameter("v");
		
		if (name == null || validStr == null) {
			res.setStatus(403); // forbidden
			res.getWriter().write("Invalid Access.");
			return;
		}
		User user;
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			session.beginTransaction();
			user = DaoUtil.getUserDao().getByEncryptName(name);
			
			if (user == null || !user.getValidationStr().equals(validStr)) {
				res.getWriter().write("Invalid validation info.");
			} else {
				user.setValidated(true);
				DaoUtil.getUserDao().save(user);
				res.getWriter().write("Validation OK");
			}
			session.getTransaction().commit();
		} catch (ItemPersistenceException e) {
			logger.warn("DAO error.", e);
			res.setStatus(500);  // Internal error
			res.getWriter().write("Internal server error. Please contact admin.");
		} catch (StaleDataException e) {
			logger.warn("DAO error.", e);
			res.setStatus(500);  // Internal error
			res.getWriter().write("Internal server error. Please contact admin.");
		} catch (HibernateException e) {
			logger.warn("Hibernate Error.", e);
			res.setStatus(500);  // Internal error
			res.getWriter().write("Internal server error. Please contact admin.");
			session.getTransaction().rollback();
			session.close();
		}
		
	}

	

}
