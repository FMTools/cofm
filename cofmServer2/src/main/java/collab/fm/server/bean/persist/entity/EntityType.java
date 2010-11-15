package collab.fm.server.bean.persist.entity;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.persist.ElementType;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.bean.transfer.EntityType2;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.DataItemUtil;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class EntityType extends ElementType {

	protected List<AttributeType> attrDefs = new ArrayList<AttributeType>();
	protected Model model;
	
	public AttributeType findAttributeTypeDef(Long attrId, boolean immediate) {
		for (EntityType et = this; 
			et != null; ) {
			for (AttributeType a: et.getAttrDefs()) {
				if (a.getId().equals(attrId)) {
					return a;
				}
			}
			try {
				if (!immediate && et.getSuperType() != null) {
					et = DaoUtil.getEntityTypeDao().getById(et.getSuperType().getId(), false);
				} else {
					return null;
				}
			} catch (ItemPersistenceException e) {
				return null;
			} catch (StaleDataException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		return null;
	}
	
	public AttributeType findAttributeTypeDef(String attrName, boolean immediate) {
		for (EntityType et = this; 
			et != null;) {
			for (AttributeType a: et.getAttrDefs()) {
				if (a.getAttrName().equals(attrName)) {
					return a;
				}
			}
			try {
				if (!immediate && et.getSuperType() != null) {
					et = DaoUtil.getEntityTypeDao().getById(et.getSuperType().getId(), false);
				} else {
					return null;
				}
			} catch (ItemPersistenceException e) {
				return null;
			} catch (StaleDataException e) {
				// TODO Auto-generated catch block
				return null;
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
