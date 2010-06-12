package collab.fm.server.util;

public class RequestGenerator {
	
	private static final String REQUEST_BEGIN = 
		"{id:1,name:\"#name#\",requesterId:#userId#,modelId:#modelId#";
	private static final String REQUEST_END = "}";
	
	private static final String OP_BEGIN = 
		"operation:{name:\"#opName#\",vote:#vote#,userid:#userId#,modelId:#modelId#";
	private static final String OP_END = "}";
	
	public static int modelId;
	public static int userId;
	
	public static String createModel(String name, String des) {
		return REQUEST_BEGIN.replace("#name#", Resources.REQ_CREATE_MODEL)
			.replace("#modelId#", "null").replace("#userId#", ""+userId) +
			",modelName:\"" + name + "\"" +
			",description:\"" + des + "\"" + REQUEST_END;
	}
	
	public static String createFeature(String name) {
		return beforeOp() + "," + 
				OP_BEGIN.replace("#opName#", Resources.OP_CREATE_FEATURE)
				.replace("#vote#", "true")
				.replace("#userId#", ""+userId).replace("#modelId#", "" + modelId) +
				",value:\"" + name + "\"" + OP_END +
			REQUEST_END;
	}
	
	public static String createBinaryRelationship(String type, int left, int right) {
		return beforeOp() + "," +
				OP_BEGIN.replace("#opName#", Resources.OP_CREATE_RELATIONSHIP)
				.replace("#vote#", "true")
				.replace("#userId#", ""+userId).replace("#modelId#", "" + modelId) +
				",type:\"" + type + "\"" + 
				",leftFeatureId:" + Integer.toString(left) +
				",rightFeatureId:" + Integer.toString(right) + OP_END +
			REQUEST_END;
	}
	
	private static String beforeOp() {
		return REQUEST_BEGIN.replace("#name#", Resources.REQ_COMMIT)
		.replace("#modelId#", "" + modelId).replace("#userId#", ""+userId);
	}
	
	public static String register(String user, String pwd) {
		return REQUEST_BEGIN.replace("#name#", Resources.REQ_REGISTER)
			.replace("#modelId#", "null").replace("#userId#", "null") + 
			",user=\"" + user + "\"" +
			",pwd=\"" + pwd + "\"" +
			REQUEST_END;
	}
	
	public static String login(String user, String pwd) {
		return REQUEST_BEGIN.replace("#name#", Resources.REQ_LOGIN)
			.replace("#modelId#", "null").replace("#userId#", "null") + 
			",user=\"" + user + "\"" +
			",pwd=\"" + pwd + "\"" +
			REQUEST_END;
	}
}
