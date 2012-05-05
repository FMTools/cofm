package collab.fm.server.bean.persist;

import java.util.Date;

import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.EntityType;

public class History {

	public static final int OP_VIEW = 0;
	public static final int OP_CREATE = 1;
	public static final int OP_SELECT = 2;
	public static final int OP_DENY = 3;
	public static final int OP_MODIFY = 4;
	
	public static final int ITEM_ENTYPE = 0;
	public static final int ITEM_FEATURE = 1;
	public static final int ITEM_RELATION = 2;
	public static final int ITEM_ATTR_DEF = 3;
	public static final int ITEM_ATTR_VALUE = 4;
	
	private Date time;
	private int op;
	private int item;
	private Model model;
	private Element element;
	private AttributeType attrType;
	private EntityType enType;
	
	public Model getModel() {
		return model;
	}
	public void setModel(Model model) {
		this.model = model;
	}
	public Element getElement() {
		return element;
	}
	public void setElement(Element element) {
		this.element = element;
	}
	public AttributeType getAttrType() {
		return attrType;
	}
	public void setAttrType(AttributeType attrType) {
		this.attrType = attrType;
	}
	public EntityType getEnType() {
		return enType;
	}
	public void setEnType(EntityType enType) {
		this.enType = enType;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public Date getTime() {
		return time;
	}
	public void setOp(int op) {
		this.op = op;
	}
	public int getOp() {
		return op;
	}
	public void setItem(int item) {
		this.item = item;
	}
	public int getItem() {
		return item;
	}
}
