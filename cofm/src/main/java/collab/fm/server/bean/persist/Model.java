package collab.fm.server.bean.persist;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.EntityType;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.bean.transfer.Model2;

public class Model extends DataItem {
	private static Logger logger = Logger.getLogger(Model.class);
	
	private String name;
	private String description;
	
	// model-to-type: one to many association
	private Set<EntityType> entityTypes = new HashSet<EntityType>();
	
	// model-to-element: one to many association
	private Set<Entity> entities = new HashSet<Entity>();
	private Set<Relation> relations = new HashSet<Relation>();
	
	// Contributors of this model (many to many)
	private Set<User> users = new HashSet<User>();
	
	@Override
	public void transfer(DataItem2 m) {
		Model2 m2 = (Model2) m;
		super.transfer(m2);
		m2.setName(this.getName());
		m2.setDes(this.getDescription());
		for (User u: this.getUsers()) {
			m2.addUser(u.getId());
		}
	}
	
	@Override
	public String toValueString() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
		return this.getName();
	}
	
	public void addEntity(Entity e) {
		this.getEntities().add(e);
		e.setModel(this);
	}
	
	public void addRelation(Relation r) {
		this.getRelations().add(r);
		r.setModel(this);
	}
	
	public void addEntityType(EntityType t) {
		this.getEntityTypes().add(t);
		t.setModel(this);
	}
	
	public void addUser(User u) {
		this.getUsers().add(u);
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

	public Set<EntityType> getEntityTypes() {
		return entityTypes;
	}

	public void setEntityTypes(Set<EntityType> entityTypes) {
		this.entityTypes = entityTypes;
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

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

}
