package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.entity.Model;
import collab.fm.server.util.exception.BeanPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface ModelDao extends GenericDao<Model, Long> {
	public List getAll() throws BeanPersistenceException, StaleDataException;
	
	public List getBySimilarName(String name) throws BeanPersistenceException, StaleDataException;
	public Model getByName(String name) throws BeanPersistenceException, StaleDataException;
}
