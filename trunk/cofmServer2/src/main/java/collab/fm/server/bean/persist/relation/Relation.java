package collab.fm.server.bean.persist.relation;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public abstract class Relation extends Element {
	
	private static Logger logger = Logger.getLogger(Relation.class);
	
	protected Model model;
	
	// Involved entities
	protected Set<Entity> entities = new HashSet<Entity>();

	public String toValueString() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
		return this.entities.toString();
	}
	
	// Voting YES to relation needs YES-vote inference, 
	// i.e. voting YES to all involved entities.
	@Override
	public int vote(boolean yes, Long userId) {
		if (yes) {
			// vote inference
			for (Entity f: this.getEntities()) {
				f.vote(true, userId);
				try {
					DaoUtil.getFeatureDao().save(f);
				} catch (EntityPersistenceException e) {
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

	public Set<Entity> getEntities() {
		return entities;
	}

	public void setEntities(Set<Entity> entities) {
		this.entities = entities;
	}

}
