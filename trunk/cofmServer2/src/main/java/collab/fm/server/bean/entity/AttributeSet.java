package collab.fm.server.bean.entity;

import collab.fm.server.bean.entity.attr.Attribute;

public interface AttributeSet {
	public boolean voteOrAddValue(String attrName, String val, boolean yes, Long userId);
	public void addAttribute(Attribute a);
	public Attribute getAttribute(String attrName);
}
