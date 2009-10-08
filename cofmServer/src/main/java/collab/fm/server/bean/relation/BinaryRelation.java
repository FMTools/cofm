package collab.fm.server.bean.relation;

import collab.fm.server.bean.Votable;

public class BinaryRelation extends Votable<Boolean> {
	
	private int first;
	private int second;
	private int type;
	
	public BinaryRelation() {
		setValue(Boolean.TRUE);
	}
	
	public BinaryRelation(int type, int first, int second) {
		setType(type);
		setFirst(first);
		setSecond(second);
		setValue(Boolean.TRUE);
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
}
