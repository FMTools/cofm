package collab.fm.server.bean.entity;

import collab.fm.server.bean.entity.attr.Attribute;

public interface AttributeSet {
	public Attribute getAttribute(String name);
	public boolean voteOrAddValue(String attrName, String val, boolean yes, Long userId);
	public void addAttribute(Attribute a);
}
