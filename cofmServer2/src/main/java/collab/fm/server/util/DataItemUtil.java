package collab.fm.server.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import collab.fm.server.bean.persist.DataItem;
import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.bean.protocol.Request;

public class DataItemUtil {

	public static void setNewDataItemByUserId(DataItem di, Long userid) {
		di.setCreator(userid);
		di.setLastModifier(userid);
		di.setCreateTime(new Date());
	}
	
	public static void addRelationForElement(Element e, Relation r) {
		if (e instanceof Entity) {
			((Entity) e).addRelationship(r);
		} else if (e instanceof Relation) {
			Relation rel = (Relation) e;
			for (Element elem: rel.getElements()) {
				addRelationForElement(elem, r);
			}
		}
	}
	
	public static void generateInferVotes(Relation relation, List<Long> votes) {
		for (Element e: relation.getElements()) {
			votes.add(e.getId());
			if (e instanceof Relation) {
				generateInferVotes((Relation) e, votes);
			}
		}
	}
	
}
