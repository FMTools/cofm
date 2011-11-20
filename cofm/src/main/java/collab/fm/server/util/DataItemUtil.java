package collab.fm.server.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import collab.fm.server.bean.persist.DataItem;
import collab.fm.server.bean.persist.Element;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.bean.persist.relation.BinRelation;
import collab.fm.server.bean.persist.relation.Relation;

public class DataItemUtil {

	public static String generateMD5(String data) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");

			byte byteData[] = md.digest(data.getBytes("UTF-8"));

			//convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
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
	
	public static void generateInferVotes(Relation relation, List<Long> votesOnEntity, List<Long> votesOnRelation) {
		for (Element e: relation.getElements()) {
			if (e instanceof Relation) {
				votesOnRelation.add(e.getId());
				generateInferVotes((Relation) e, votesOnEntity, votesOnRelation);
			} else if (e instanceof Entity) {
				votesOnEntity.add(e.getId());
			}
		}
	}
	
	public static String formatDate(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(d);
	}
}
