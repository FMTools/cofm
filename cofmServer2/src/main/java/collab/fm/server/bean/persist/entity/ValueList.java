package collab.fm.server.bean.persist.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * A mediate-class used for defining Map<key, List<value>> in the Entity class. Because
 * Hibernate doesn't support collections like Map<key, List<value>>, we have to
 * define a ValueList to wrap the List<value> in the Map.
 * @author mark
 *
 */
public class ValueList {
	private Long id;
	
	private List<Value> values = new ArrayList<Value>();
	
	public ValueList() {
		
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Value> getValues() {
		return values;
	}

	public void setValues(List<Value> values) {
		this.values = values;
	}
	
}
