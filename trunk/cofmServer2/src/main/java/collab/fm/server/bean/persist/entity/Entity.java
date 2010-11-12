package collab.fm.server.bean.persist.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.Comment;
import collab.fm.server.bean.persist.DataItem;
import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.bean.transfer.Comment2;
import collab.fm.server.bean.transfer.DataItem2;
import collab.fm.server.bean.transfer.Entity2;
import collab.fm.server.bean.transfer.Value2;
import collab.fm.server.bean.transfer.ValueList2;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.DataItemUtil;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class Entity extends Element {
	
	private static Logger logger = Logger.getLogger(Entity.class);
	
	// Attribute-Value map of this entity. Key = AttrDefId.
	protected Map<Long, ValueList> attrs = new HashMap<Long, ValueList>();
	
	protected Model model;
	
	protected List<Comment> comments = new ArrayList<Comment>();

	@Override
	public String toValueString() {
		if (this.getId() != null) {
			return this.getId().toString();
		}
		return String.valueOf(this.getAttrs().hashCode());
	}
	
	// Voting NO to an entity needs NO-votes inference, 
	// i.e. voting NO to all involved relationships.
	@Override
	public int vote(boolean yes, Long userId) {
		if (!yes) {
			// vote inference
			for (Relation r: rels) {
				try {
					if (r.vote(false, userId) == DataItem.REMOVAL_EXECUTED) {
						DaoUtil.getRelationDao().delete(r);
					} else {
						DaoUtil.getRelationDao().save(r);
					}
				} catch (ItemPersistenceException e) {
					logger.warn("Vote on relationship failed.", e);
				} catch (StaleDataException e) {
					logger.warn("Vote on relationship failed.", e);
				}
			}
		}
		return super.vote(yes, userId);
	}
	
	// Return Votable.CREATION_EXECUTED (if added new value) or VOTE_EXECUTED (if voted on existing value).
	public int voteOrAddValue(Long attrId, String value, boolean yes, Long userId) {
		// Check the validity of the value.
		AttributeType atype = ((EntityType)this.getType()).findAttributeTypeDef(attrId);
		if (atype == null || !atype.valueConformsToType(value)) {
			return DataItem.INVALID_OPERATION;
		}
		
		// Get the value list of the attribute, if such attribute doesn't exist in this.getAttrs(),
		// it means that the entity has no value assigned for the attribute, then we create
		// the attribute entry first.
		ValueList list = this.getAttrs().get(attrId);
		if (list == null) {
			list = new ValueList();
			this.getAttrs().put(attrId, list);
		}
		
		// Try voting operation.
		boolean isVoting = false;
		for (Iterator<Value> it = list.getValues().iterator(); it.hasNext();) {
			Value v = it.next();
			int execOp = DataItem.EMPTY_OPERATION;
			if (v.toValueString().equals(value)) {
				isVoting = true;
				execOp = v.vote(yes, userId);
			} else if (!atype.isMultipleSupport() && yes) {
				// If multipleSupport is disabled and this vote is YES, then we auto vote NO to other values
				// (NOTE: if this vote is NO, we do nothing.)
				execOp = v.vote(false, userId);
			}
			if (execOp == DataItem.REMOVAL_EXECUTED) {
				// If there's no supporters after the vote, then remove this value.
				it.remove();
			}
		}
		
		if (!isVoting) {
			// The value does not exist, we create it here.
			Value theVal = new Value();
			DataItemUtil.setNewDataItemByUserId(theVal, userId);
			theVal.setVal(value);
			theVal.vote(true, userId);
			list.getValues().add(theVal);
			return DataItem.CREATION_EXECUTED;
		}
		return DataItem.VOTE_EXECUTED;
	}
	
	@Override
	public void transfer(DataItem2 f) {
		Entity2 that = (Entity2)f;
		super.transfer(that);
		that.setModel(this.getModel().getId());

		for (Comment c: this.getComments()) {
			Comment2 c2 = new Comment2();
			c.transfer(c2);
			that.getComments().add(c2);
		}
		
		for (Map.Entry<Long, ValueList> v: this.getAttrs().entrySet()) {
			ValueList2 v2 = new ValueList2();
			v2.setAttrId(v.getKey());
			for (Value value: v.getValue().getValues()) {
				Value2 value2 = new Value2();
				value.transfer(value2);
				v2.getVals().add(value2);
			}
		}
	}
	
	public void addComment(Comment c) {
		this.getComments().add(c);
	}
	
	public void addRelationship(Relation r) {
		this.getRels().add(r);
	}
	
	public Map<Long, ValueList> getAttrs() {
		return attrs;
	}

	public void setAttrs(Map<Long, ValueList> attrs) {
		this.attrs = attrs;
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

}
