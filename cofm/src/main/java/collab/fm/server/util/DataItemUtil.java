package collab.fm.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import collab.fm.server.bean.persist.DataItem;
import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.entity.EnumAttributeType;
import collab.fm.server.bean.persist.entity.NumericAttributeType;
import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.Relation;
import collab.fm.server.bean.transfer.AttributeType2;
import collab.fm.server.bean.transfer.EnumAttributeType2;
import collab.fm.server.bean.transfer.NumericAttributeType2;

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
	
	// Is "r" a binary relation between "e1" and "e2" ?
	public static boolean isBinRelationBetween(Relation r, Entity e1, Entity e2) {
		if (!(r instanceof BinRelation)) {
			return false;
		}
		BinRelation br = (BinRelation) r;
		return (br.getSourceId().equals(e1.getId()) && br.getTargetId().equals(e2.getId()))
		|| (br.getSourceId().equals(e2.getId()) && br.getTargetId().equals(e1.getId()));
	}
	
	public static void generateInferVotes(Relation relation, List<Long> votes) {
		for (Element e: relation.getElements()) {
			votes.add(e.getId());
			if (e instanceof Relation) {
				generateInferVotes((Relation) e, votes);
			}
		}
	}
	
	public static String formatDate(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d);
	}
	
	public static AttributeType2 transferAttributeType(AttributeType t) {
		AttributeType2 rslt = null;
		if (t.getTypeName().equals(AttributeType.TYPE_ENUM)) {
			rslt = new EnumAttributeType2();
			((EnumAttributeType)t).transfer(rslt);
		} else if (t.getTypeName().equals(AttributeType.TYPE_NUMBER)) {
			rslt = new NumericAttributeType2();
			((NumericAttributeType)t).transfer(rslt);
		} else {
			rslt = new AttributeType2();
			t.transfer(rslt);
		}
		return rslt;
	}
}
