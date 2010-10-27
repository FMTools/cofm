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
	
	protected Model model;

	@Override
	public void transfer(DataItem2 target) {
		//TODO: define class ElementType2 and set the typeName of e2
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

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

}
