package collab.fm.server.tool;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;

import collab.fm.server.bean.persist.*;
import collab.fm.server.persistence.HibernateUtil;
import collab.fm.server.util.*;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

/**
 * Encrypt password for early users. (Their passwords are not encrypted.)
 * 
 */
public class PasswordEncryptor {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] argv) {
		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
		try {
			
			session.beginTransaction();
			
			List<User> users = DaoUtil.getUserDao().getAll();
			for (User user: users) {
				if (user.getPasswordInMD5() == null || user.getPasswordInMD5().isEmpty()) {
					String md5 = DataItemUtil.generateMD5(user.getPassword());
					user.setPasswordInMD5(md5);
				}
				if (user.isValidated() == null) {
					user.setValidated(true);
				}
				DaoUtil.getUserDao().save(user);
			}
			
			session.getTransaction().commit();
		} catch (ItemPersistenceException e) {
			System.out.println("Error");
		} catch (StaleDataException e) {
			System.out.println("Error");
		} catch (HibernateException he) {
			session.getTransaction().rollback();
			System.out.println("Database error.");
			session.close();
		} 
		
	}
}
