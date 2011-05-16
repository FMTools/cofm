package collab.fm.server.persistence;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.StaleObjectStateException;

import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class RelationDaoImpl extends GenericDaoImpl<Relation, Long>
		implements RelationDao {

	static Logger logger = Logger.getLogger(RelationDaoImpl.class);

	public List<Relation> getByExample(Long modelId, BinRelation example)
			throws ItemPersistenceException, StaleDataException {
		try {
			List result = HibernateUtil.getCurrentSession()
				.createQuery("select rel from BinRelation as rel " +
						"join rel.model as m " +  // m.class == Model
						"join rel.type as t " +  // t.class == ElementType
						"where m.id = :mId " +
						"and t.typeName = :type " +
						"and rel.sourceId = :source " +
						"and rel.targetId = :target")
				.setLong("mId", modelId)
				.setString("type", example.getType().getTypeName())
				.setLong("source", example.getSourceId())
				.setLong("target", example.getTargetId())
				.list();
			return result.isEmpty() ? null : (List<Relation>)result;
		} catch (StaleObjectStateException sose) {
			logger.warn("Stale data detected. Force client to retry.", sose);
			throw new StaleDataException(sose);
		} catch (RuntimeException e) {
			logger.warn("Query failed.", e);
			throw new ItemPersistenceException("Query failed.", e);
		}
	}

}
