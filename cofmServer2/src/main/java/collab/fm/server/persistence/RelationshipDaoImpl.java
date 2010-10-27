package collab.fm.server.persistence;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.StaleObjectStateException;

import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class RelationshipDaoImpl extends GenericDaoImpl<Relation, Long>
		implements RelationshipDao {

	static Logger logger = Logger.getLogger(RelationshipDaoImpl.class);

	public List<Relation> getByExample(Long modelId, BinRelation example)
			throws EntityPersistenceException, StaleDataException {
		try {
			List result = HibernateUtil.getCurrentSession()
				.createQuery("select rel from BinaryRelationship as rel " +
						"join rel.model as m " +
						"where m.id = :mId " +
						"and rel.type = :type " +
						"and rel.leftFeatureId = :left " +
						"and rel.rightFeatureId = :right")
				.setLong("mId", modelId)
				.setString("type", example.getType())
				.setLong("left", example.getLeftFeatureId())
				.setLong("right", example.getRightFeatureId())
				.list();
			return result.isEmpty() ? null : (List<Relation>)result;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (RuntimeException e) {
			logger.warn("Query failed.", e);
			throw new EntityPersistenceException("Query failed.", e);
		}
	}

	public List getAll(Long modelId) throws EntityPersistenceException,
			StaleDataException {
		return super.getAll(modelId, "model");
	}

}
