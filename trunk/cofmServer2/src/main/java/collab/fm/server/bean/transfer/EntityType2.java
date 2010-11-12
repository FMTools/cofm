package collab.fm.server.bean.transfer;

import java.util.ArrayList;
import java.util.List;

public class EntityType2 extends DataItem2 {
	private String typeName;
	private Long superId;
	private List<AttributeType2> attrDefs = new ArrayList<AttributeType2>();
	private Long model;
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public Long getSuperId() {
		return superId;
	}
	public void setSuperId(Long superId) {
		this.superId = superId;
	}
	public List<AttributeType2> getAttrDefs() {
		return attrDefs;
	}
	public void setAttrDefs(List<AttributeType2> attrDefs) {
		this.attrDefs = attrDefs;
	}
	public Long getModel() {
		return model;
	}
	public void setModel(Long model) {
		this.model = model;
	}
	
	
}
