package collab.fm.server.bean.persist.entity;

import java.util.HashMap;
import java.util.Map;

import collab.fm.server.bean.persist.ElementType;
import collab.fm.server.bean.transfer.DataItem2;

public class EntityType extends ElementType {

	protected Map<String, AttributeType> attrDefs = new HashMap<String, AttributeType>();

	@Override
	public void transfer(DataItem2 target) {
		//TODO:
		super.transfer(target);
	}
	
	public Map<String, AttributeType> getAttrDefs() {
		return attrDefs;
	}

	public void setAttrDefs(Map<String, AttributeType> attrDefs) {
		this.attrDefs = attrDefs;
	}
}
