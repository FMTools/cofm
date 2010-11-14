package collab.fm.server.bean.persist.entity;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.persist.ElementType;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.bean.transfer.EntityType2;
import collab.fm.server.util.DataItemUtil;

public class EntityType extends ElementType {

	protected List<AttributeType> attrDefs = new ArrayList<AttributeType>();
	protected Model model;
	
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
		EntityType2 that = (EntityType2)target;
		super.transfer(that);
		that.setTypeName(this.getTypeName());
		if (this.getSuperType() != null) {
			that.setSuperId(this.getSuperType().getId());
		} else {
			that.setSuperId(null);
		}
		that.setModel(this.getModel().getId());
		for (AttributeType t: this.getAttrDefs()) {
			that.getAttrDefs().add(DataItemUtil.transferAttributeType(t));
		}
	}

	public List<AttributeType> getAttrDefs() {
		return attrDefs;
	}

	public void setAttrDefs(List<AttributeType> attrDefs) {
		this.attrDefs = attrDefs;
	}
	
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
