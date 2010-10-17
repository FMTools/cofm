package collab.fm.server.bean.persist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.Value;
import collab.fm.server.bean.transfer.Comment2;
import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.bean.transfer.Feature2;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.EntityUtil;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class Feature extends Element {
	
	private static Logger logger = Logger.getLogger(Feature.class);
	
	// Attributes: key = Attr_Name
	private Map<String, AttributeType> attrs = new HashMap<String, AttributeType>();
	
	// Involved relationships.
	private Set<Relationship> rels = new HashSet<Relationship>();
	
	private Model model;
	
	// Comments about a feature
	private List<Comment> comments = new ArrayList<Comment>();
	
	public Feature() {
		super();
	}
	
	public Feature(Long creator) {
		super(creator);
	}

	public void addComment(Comment c) {
		this.getComments().add(c);
	}
	
	public void addRelationship(Relationship r) {
		this.getRels().add(r);
	}
	
	public void addAttribute(AttributeType a) {
		if (attrs.get(a.getName()) == null) {
			attrs.put(a.getName(), a);
		}
	}
	
	public boolean voteOrAddValue(String attrName, String val, boolean yes, Long userId) {
		AttributeType attr = attrs.get(attrName);
		if (attr == null) {
			return false;
		}
		Value v = new Value(userId);
		v.setStrVal(val);
		return attr.voteOrAddValue(v, yes, userId);
	}
	
	// Voting NO to a feature needs vote inference, i.e. voting NO to all involved relationships.
	@Override
	public boolean vote(boolean yes, Long userId) {
		if (!yes) {
			// vote inference
			for (Relationship r: rels) {
				try {
					if (r.vote(false, userId)) {
							DaoUtil.getRelationshipDao().save(r);
						
					} else {
						DaoUtil.getRelationshipDao().delete(r);
					}
				} catch (EntityPersistenceException e) {
					logger.warn("Vote on relationship failed.", e);
				} catch (StaleDataException e) {
					logger.warn("Vote on relationship failed.", e);
				}
			}
		}
		return super.vote(yes, userId);
	}
	
	public Map<String, AttributeType> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<String, AttributeType> attrs) {
		this.attrs = attrs;
	}

	public Set<Relationship> getRels() {
		return rels;
	}

	public void setRels(Set<Relationship> rels) {
		this.rels = rels;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (this == null || o == null) return false;
		if (!(o instanceof Feature)) return false;
		Feature that = (Feature) o;
		return this.toValueString().equals(that.toValueString());
	}

	@Override
	public int hashCode() {
		return this.toValueString().hashCode();
	}

	@Override
	public String toValueString() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
		return String.valueOf(this.getAttrs().hashCode());
	}
	
	@Override
	protected void removeThis() {
		try {
			DaoUtil.getFeatureDao().delete(this);
		} catch (EntityPersistenceException e) {
			logger.warn("Delete feature failed.", e);
		} catch (StaleDataException e) {
			logger.warn("Delete feature failed.", e);
		}
	}

	public AttributeType getAttribute(String attrName) {
		return attrs.get(attrName);
	}
	
	@Override
	public void transfer(Entity2 f) {
		Feature2 f2 = (Feature2) f;
		super.transfer(f2);
		f2.setModel(this.getModel().getId());
		
		for (Relationship r: this.getRels()) {
			f2.addRel(r.getId());
		}
		
		for (Comment c: this.getComments()) {
			Comment2 c2 = new Comment2();
			c.transfer(c2);
			f2.addComment(c2);
		}
		
		for (Map.Entry<String, AttributeType> e: this.getAttrs().entrySet()) {
			f2.addAttr(EntityUtil.transferFromAttr(e.getValue()));
		}
	}
}
