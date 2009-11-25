package collab.fm.server.persistence;

import java.util.List;

public interface GenericDao<BeanType, IdType> {
	
	public BeanType getById(IdType id);
	
	public List<BeanType> getAll();
	
}
