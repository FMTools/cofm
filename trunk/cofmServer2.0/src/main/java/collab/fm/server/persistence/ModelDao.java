package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.entity.Model;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface ModelDao extends GenericDao<Model, Long> {
	public List getAll() throws EntityPersistenceException, StaleDataException;
	
	public List getBySimilarName(String name) throws EntityPersistenceException, StaleDataException;
	public Model getByName(String name) throws EntityPersistenceException, StaleDataException;
}
