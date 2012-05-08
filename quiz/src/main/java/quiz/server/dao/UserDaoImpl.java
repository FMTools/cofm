package quiz.server.dao;

import org.hibernate.Query;
import org.hibernate.StaleObjectStateException;

import edu.stanford.nlp.util.StringUtils;
import quiz.server.bean.User;

public class UserDaoImpl extends GenericDaoImpl<User> implements UserDao {

	protected User getByAttrValuePairs(Pair<String, String>[] items) {
		try {
			String[] conds = new String[items.length];
			for (int i = 0; i < items.length; i++) {
				conds[i] =  "user." + items[i].first + " = :" + items[i].first + "Val"; 
			}
			Query query = HibernateUtil.getCurrentSession()
				.createQuery("from User as user where " + StringUtils.join(conds, " and "));
			for (int i = 0; i < items.length; i++) {
				query.setString(items[i].first + "Val", items[i].second);
			}
			return (User) query.uniqueResult();
		} catch (StaleObjectStateException sose) {
			return null;
		} catch(RuntimeException e) {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public User getByName(String name) {
		return getByAttrValuePairs(new Pair[] {
				Pair.make("name", name)
			});
	}

	@SuppressWarnings("unchecked")
	public User getByNameAndVCode(String name, String vcode) {
		return getByAttrValuePairs(new Pair[] {
				Pair.make("name", name),
				Pair.make("vcode", vcode)
			});
	}

}
