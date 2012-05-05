package collab.fm.server.bean.persist.relation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.bean.transfer.Relation2;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.DataItemUtil;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class Relation extends Element {
	
	private static Logger logger = Logger.getLogger(Relation.class);
	
	public static final String[] predicateName = {
		"opt", "requires", "excludes", "none", "all", "single", "or"
	};
	public static final int OPTIONAL = 0;
	public static final int REQUIRE = 1;
	public static final int EXCLUDE = 2;
	public static final int NONE = 3;
	public static final int ALL = 4;
	public static final int SINGLE = 5;
	public static final int OR = 6;
			
	protected Model model;
	
	protected String signature;
	
	// Optional: relation name
	protected String name;
	
	// Do not store elements in database
	protected List<Element> elements = new ArrayList<Element>(); 
	
	protected Set<Entity> entities = new HashSet<Entity>();
	
	// Do not store predicate in database
	protected int predicate;
	
	protected boolean refine;

	public Relation() {
		
	}
	
	public void addElement(Element e) {
		elements.add(e);
	}
	
	public void computeSignature() {
		entities.clear();
		for (Element e: elements) {
			entities.addAll(DataItemUtil.linkRelationToEntity(e, this));
		}
		signature = Relation.toExp(this);
	}
	
	public boolean isBinary() {
		return this.signature.matches("^\\d+\\(\\d+,\\d+\\)$");
	}
	
	public boolean containsParent(Entity e) {
		if (this.isRefine()) {
			return getParentId() == e.getId();
		}
		return false;
	}
	
	public boolean containsChild(Entity e) {
		if (this.isRefine()) {
			long id = getParentId();
			if (e.getId() == id) {
				return false;
			}
			for (Entity en: entities) {
				if (en.getId() == e.getId()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public Entity getParent() {
		if (this.isRefine()) {
			long id = getParentId();
			for (Entity e: entities) {
				if (e.getId() == id) {
					return e;
				}
			}
		}
		return null;
	}
	
	public long getParentId() {
		int begin = signature.indexOf('(');
		begin++;
		int end = begin;
		while (isDigit(signature.charAt(end))) {
			end++;
		}
		return Long.valueOf(signature.substring(begin, end));
	}
	
	public List<Long> getChildrenId() {
		List<Long> result = new ArrayList<Long>();
		long parent = getParentId();
		for (Entity e: entities) {
			if (e.getId() != parent) {
				result.add(e.getId());
			}
		}
		return result;
	}
	
	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}
	
	@Override
	public void transfer(DataItem2 d) {
		Relation2 that = (Relation2) d;
		that.setSignature(this.getSignature());
	}
	
	public String toValueString() {
		return signature;
	}
	
	public static String toExp(Element top) {
		if (top instanceof Entity) {
			return top.getId().toString();
		}
		Relation r = (Relation) top;
		StringBuilder b = new StringBuilder();
		b.append(r.getPredicate() + "(");
		int i = 0;
		for (Element e: r.getElements()) {
			if (i++ > 0) {
				b.append(",");
			}
			b.append(toExp(e));
		}
		b.append(")");
		return b.toString();
	}
	
	// Voting YES to relation needs YES-vote inference, 
	// i.e. voting YES to all involved entities.
	@Override
	public int vote(boolean yes, Long userId) {
		if (yes) {
			// vote inference
			for (Element f: this.getElements()) {
				f.vote(true, userId);
				try {
					DaoUtil.getElementDao().save(f);
				} catch (ItemPersistenceException e) {
					logger.warn("Vote on entity failed.", e);
				} catch (StaleDataException e) {
					logger.warn("Vote on entity failed.", e);
				}
			}
		}
		return super.vote(yes, userId);
	}
	
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPredicate() {
		return predicate;
	}

	public void setPredicate(int predicate) {
		this.predicate = predicate;
	}

	public boolean isRefine() {
		return refine;
	}

	public void setRefine(boolean refine) {
		this.refine = refine;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Set<Entity> getEntities() {
		return entities;
	}

	public void setEntities(Set<Entity> entities) {
		this.entities = entities;
	}

	
}
