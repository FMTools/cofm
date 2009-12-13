package collab.fm.server.persistence;

import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.BinaryRelationship;
import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.util.exception.BeanPersistenceException;

public class RelationshipDaoImpl extends GenericDaoImpl<Relationship, Long>
		implements RelationshipDao {

	static Logger logger = Logger.getLogger(RelationshipDaoImpl.class);

	public List<Relationship> getByExample(BinaryRelationship example)
			throws BeanPersistenceException {
		try {
			List result = HibernateUtil.getCurrentSession()
				.createQuery("from BinaryRelationship as rel " +
						"where rel.type = :type " +
						"and rel.leftFeatureId = :left " +
						"and rel.rightFeatureId = :right")
				.setString("type", example.getType())
				.setLong("left", example.getLeftFeatureId())
				.setLong("right", example.getRightFeatureId())
				.list();
			return result.isEmpty() ? null : (List<Relationship>)result;
		} catch (RuntimeException e) {
			logger.warn("Query failed.", e);
			throw new BeanPersistenceException("Query failed.", e);
		}
	}

}
