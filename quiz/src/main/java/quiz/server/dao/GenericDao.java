package quiz.server.dao;

import java.util.List;

public interface GenericDao<DataType> {

	DataType getById(int id, boolean lock);
	
	List getAll();
	
	DataType save(DataType data);
	
	void remove(DataType data);
}
