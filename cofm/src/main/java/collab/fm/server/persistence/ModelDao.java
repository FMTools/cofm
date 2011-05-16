package collab.fm.server.persistence;

import java.util.List;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public interface ModelDao extends GenericDao<Model, Long> {
	public List getBySimilarName(String name) throws ItemPersistenceException, StaleDataException;
	
	public Model getByName(String name) throws ItemPersistenceException, StaleDataException;
}
