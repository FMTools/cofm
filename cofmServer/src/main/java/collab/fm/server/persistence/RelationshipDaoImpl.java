package collab.fm.server.persistence;

import java.util.List;

import org.apache.log4j.Logger;

import collab.fm.server.bean.entity.Feature;
import collab.fm.server.bean.entity.Relationship;
import collab.fm.server.util.exception.BeanPersistenceException;

public class RelationshipDaoImpl extends GenericDaoImpl<Relationship, Long>
		implements RelationshipDao {

	static Logger logger = Logger.getLogger(RelationshipDaoImpl.class);
	
	public List getByExample(Relationship example, String... excludeProperties)
			throws BeanPersistenceException {
		// By default, we exclude these 3 properties.
		if (excludeProperties == null) {
			logger.debug("Default query.");
			return super.getByExample(example, "id", "existence", "features");
		}
		return super.getByExample(example, excludeProperties);
	}

}
