package collab.fm.server.bean.persist;

import java.util.HashSet;
import java.util.Set;

import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.Value;
import collab.fm.server.bean.persist.relation.Relation;

import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.bean.transfer.PersonalView2;

public class PersonalView extends DataItem {

	private String name;
	private String description;
	
	private Model model; // the parent model
	
	// Only the selected elements are stored here.
	private Set<Entity> entities = new HashSet<Entity>();
	private Set<Relation> relations = new HashSet<Relation>();
	private Set<Value> values = new HashSet<Value>();
	
	private boolean visibleToOthers;
	
	@Override
	public void transfer(DataItem2 p) {
		PersonalView2 pv2 = (PersonalView2) p;
		super.transfer(pv2);
		pv2.setName(this.getName());
	}
	
	public void addEntity(Entity en) {
		this.entities.add(en);
		en.getViews().add(this);
	}
	
	public void removeEntity(Entity en) {
		this.entities.remove(en);
		en.getViews().remove(this);
	}
	
	public void addRelation(Relation r) {
		this.relations.add(r);
		r.getViews().add(this);
	}
	
	public void removeRelation(Relation r) {
		this.relations.remove(r);
		r.getViews().remove(this);
	}
	
	public void addValue(Value v) {
		this.values.add(v);
		v.getViews().add(this);
	}
	
	public void removeValue(Value v) {
		this.values.remove(v);
		v.getViews().remove(this);
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public Set<Entity> getEntities() {
		return entities;
	}

	public void setEntities(Set<Entity> entities) {
		this.entities = entities;
	}

	public Set<Relation> getRelations() {
		return relations;
	}

	public void setRelations(Set<Relation> relations) {
		this.relations = relations;
	}

	public boolean isVisibleToOthers() {
		return visibleToOthers;
	}

	public void setVisibleToOthers(boolean visibleToOthers) {
		this.visibleToOthers = visibleToOthers;
	}

	@Override
	public String toValueString() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
		return this.getCreator() + this.getName();
	}

	public void setValues(Set<Value> values) {
		this.values = values;
	}

	public Set<Value> getValues() {
		return values;
	}

}
