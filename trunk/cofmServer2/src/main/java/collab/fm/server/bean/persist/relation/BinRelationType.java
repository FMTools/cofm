package collab.fm.server.bean.persist.relation;

import collab.fm.server.bean.persist.ElementType;
import collab.fm.server.bean.transfer.DataItem2;

// A simple one-to-one binary relation type
public class BinRelationType extends RelationType {
	
	protected ElementType sourceType;
	protected ElementType targetType;
	
	protected boolean directed;
	
	@Override 
	public void transfer(DataItem2 item) {
		
	}

	public ElementType getSourceType() {
		return sourceType;
	}

	public void setSourceType(ElementType sourceType) {
		this.sourceType = sourceType;
	}

	public ElementType getTargetType() {
		return targetType;
	}

	public void setTargetType(ElementType targetType) {
		this.targetType = targetType;
	}

	public boolean isDirected() {
		return directed;
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}
	
}
