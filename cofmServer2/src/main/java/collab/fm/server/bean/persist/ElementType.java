package collab.fm.server.bean.persist;

import collab.fm.server.bean.transfer.DataItem2;

/**
 * Define an element type. An element can be either an entity or a relation.
 * @author mark
 *
 */
public class ElementType extends DataItem {
	protected String typeName;
	
	protected ElementType superType;
	
	@Override
	public void transfer(DataItem2 target) {
		super.transfer(target);
	}
	
	@Override
	public String toValueString() {
		return this.getTypeName();
	}
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public ElementType getSuperType() {
		return superType;
	}

	public void setSuperType(ElementType superType) {
		this.superType = superType;
	}

	

}
