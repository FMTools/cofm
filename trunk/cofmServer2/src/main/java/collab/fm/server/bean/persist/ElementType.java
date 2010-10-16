package collab.fm.server.bean.persist;

import collab.fm.server.bean.transfer.Entity2;

/**
 * Define an element type. An element can be either an entity or a relation.
 * @author mark
 *
 */
public class ElementType extends DataItem {
	protected String typeName;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	@Override
	public void transfer(Entity2 target) {
		//TODO: define class ElementType2 and set the typeName of e2
		super.transfer(target);
	}
}
