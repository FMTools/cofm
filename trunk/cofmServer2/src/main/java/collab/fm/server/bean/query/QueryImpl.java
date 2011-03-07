package collab.fm.server.bean.query;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.EntityType;
import collab.fm.server.bean.persist.entity.ValueList;
import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.BinRelationType;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.bean.persist.relation.RelationType;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

/**
 * In this query implementation, we only consider Binary Relations in the
 * query-on-relation methods.
 * 
 * @author Yi Li
 * 2010-12
 *
 */
public class QueryImpl implements IQuery {

	/**
	 * Get all children of the entity.
	 * Return null if no children.
	 */
	public List<Entity> getChildren(Long entityId) {
		try {
			
			Entity me = DaoUtil.getEntityDao().getById(entityId, false);
			if (me == null) {
				return null;
			}
			
			List<Entity> result = new ArrayList<Entity>();
			
			// Check for every hierarchical relation (i.e. refinements) connected
			// with "me".
			for (Relation rel: me.getRels()) {
				if ((rel.getType() instanceof RelationType) &&
						((RelationType)rel.getType()).isHierarchical()) {
					// Consider only binary relations here.
					if (rel instanceof BinRelation) {
						BinRelation br = (BinRelation) rel;
						// If I am a parent (i.e. the source of the br)
						if (br.getSourceId().equals(entityId)) {
							// Add this child to result
							Entity myChild = DaoUtil.getEntityDao().getById(br.getTargetId(), false);
							if (myChild != null) {
								result.add(myChild);
							}
						}
					}
				}
			}
			
			// Always returns null in all kinds of "empty queries", as in the persistence classes.
			return result.isEmpty() ? null : result;
			
		} catch (ItemPersistenceException e) {
			e.printStackTrace();
			return null;
		} catch (StaleDataException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get parent(s) of the entity, typically there is only one parent; however, in models
	 * of CoFM, we allow multiple parents be assigned to one entity, so we return a list here.
	 * 
	 * Returns null if no parents.
	 */
	public List<Entity> getParents(Long entityId) {
		try {
			
			Entity me = DaoUtil.getEntityDao().getById(entityId, false);
			if (me == null) {
				return null;
			}
			
			List<Entity> result = new ArrayList<Entity>();
			
			// Check for every hierarchical relation (i.e. refinements) connected
			// with "me".
			for (Relation rel: me.getRels()) {
				if ((rel.getType() instanceof RelationType) &&
						((RelationType)rel.getType()).isHierarchical()) {
					// Consider only binary relations here.
					if (rel instanceof BinRelation) {
						BinRelation br = (BinRelation) rel;
						// If I am a child (i.e. the target of the br)
						if (br.getTargetId().equals(entityId)) {
							// Add this parent to result
							Entity myParent = DaoUtil.getEntityDao().getById(br.getSourceId(), false);
							if (myParent != null) {
								result.add(myParent);
							}
						}
					}
				}
			}
			
			// Always returns null in all kinds of "empty queries", as in the persistence classes.
			return result.isEmpty() ? null : result;
			
		} catch (ItemPersistenceException e) {
			e.printStackTrace();
			return null;
		} catch (StaleDataException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Entity> getEntityByName(Long modelId, Long entityTypeId, String name) {
		try {
			// Get the attribute ID of the NAME attribute. (The NAME attribute is a default
			// and unchangable attribute for all entities.)		
			AttributeType attrDef = DaoUtil.getAttributeDefDao().getByAttrName(entityTypeId, Resources.ATTR_ENTITY_NAME);
			if (attrDef == null) {
				return null;
			}
			
			return DaoUtil.getEntityDao().getByAttrValue(modelId, attrDef.getId(), name, false);
			
		} catch (ItemPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public EntityType getEntityTypeByName(Long modelId, String name) {
		try {
			Model model = DaoUtil.getModelDao().getById(modelId, false);
			if (model == null) {
				return null;
			}
			for (EntityType type: model.getEntityTypes()) {
				if (type.getTypeName().equals(name)) {
					return type;
				}
			}
			return null;
			
		} catch (ItemPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public RelationType getRelationTypeByName(Long modelId, String name) {
		try {
			Model model = DaoUtil.getModelDao().getById(modelId, false);
			if (model == null) {
				return null;
			}
			for (RelationType type: model.getRelationTypes()) {
				if (type.getTypeName().equals(name)) {
					return type;
				}
			}
			return null;
			
		} catch (ItemPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Entity> getInvolvedEntities(Long entityId, String relationTypeName) {
		try {
			
			Entity me = DaoUtil.getEntityDao().getById(entityId, false);
			if (me == null) {
				return null;
			}
			
			List<Entity> result = new ArrayList<Entity>();
			for (Relation r: me.getRels()) {
				// If r is type of relationType
				if (r.getType().getTypeName().equals(relationTypeName)) {
					// Handles only binary relations here.
					if (r instanceof BinRelation) {
						BinRelation br = (BinRelation) r;
						// Add the counterpart of me in current relation.
						Entity myCounterpart = null;
						if (br.getSourceId().equals(entityId)) {
							myCounterpart = DaoUtil.getEntityDao().getById(br.getTargetId(), false);
						} else if (br.getTargetId().equals(entityId)) {
							myCounterpart = DaoUtil.getEntityDao().getById(br.getSourceId(), false);
						}
						if (myCounterpart != null) {
							result.add(myCounterpart);
						}
					}
				}
			}
			
			return result.isEmpty() ? null : result;
			
		} catch (ItemPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public List<RelationType> getInvolvedRelationTypeByEntityType(
			Long entityTypeId) {
		try {
			
			EntityType me = DaoUtil.getEntityTypeDao().getById(entityTypeId, false);
			if (me == null) {
				return null;
			}
			
			List<RelationType> result = new ArrayList<RelationType>();
			
			// For all Relation Types in current model, check to see
			// if the relation is involved the entity type.
			for (RelationType rt: me.getModel().getRelationTypes()) {
				// Handles only binary relations here.
				if (rt instanceof BinRelationType) {
					BinRelationType brt = (BinRelationType) rt;
					if (brt.getSourceType().getId().equals(entityTypeId) ||
						brt.getTargetType().getId().equals(entityTypeId)) {
						result.add(brt);
					}
				}
			}
			
			return result.isEmpty() ? null : result;
			
		} catch (ItemPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public ValueList getPossibleValues(Long entityId, String attrName) {
		try {
			Entity me = DaoUtil.getEntityDao().getById(entityId, false);
			if (me == null) {
				return null;
			}
			AttributeType attrDef = DaoUtil.getAttributeDefDao().getByAttrName(me.getType().getId(), attrName);
			if (attrDef == null) {
				return null;
			}
			return me.getAttrs().get(attrDef.getId());
			
		} catch (ItemPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (StaleDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
