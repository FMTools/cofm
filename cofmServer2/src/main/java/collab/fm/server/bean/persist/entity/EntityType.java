package collab.fm.server.bean.persist.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import collab.fm.server.bean.persist.ElementType;
import collab.fm.server.bean.transfer.DataItem2;

public class EntityType extends ElementType {

	protected List<AttributeType> attrDefs = new ArrayList<AttributeType>();

	public AttributeType findAttributeTypeDef(Long attrId) {
		for (EntityType et = this; 
			et != null; et = (EntityType) et.getSuperType()) {
			for (AttributeType a: et.getAttrDefs()) {
				if (a.getId().equals(attrId)) {
					return a;
				}
			}
		}
		return null;
	}
	
	public AttributeType findAttributeTypeDef(String attrName) {
		for (EntityType et = this; 
			et != null; et = (EntityType) et.getSuperType()) {
			for (AttributeType a: et.getAttrDefs()) {
				if (a.getAttrName().equals(attrName)) {
					return a;
				}
			}
		}
		return null;
	}
	
	@Override
	public void transfer(DataItem2 target) {
		//TODO:
		super.transfer(target);
	}

	public List<AttributeType> getAttrDefs() {
		return attrDefs;
	}

	public void setAttrDefs(List<AttributeType> attrDefs) {
		this.attrDefs = attrDefs;
	}
	
}
