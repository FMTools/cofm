package collab.fm.server.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import collab.fm.server.bean.operation.BinaryRelationshipOperation;
import collab.fm.server.bean.operation.FeatureOperation;
import collab.fm.server.bean.operation.Operation;
import collab.fm.server.bean.operation.RelationshipOperation;
import collab.fm.server.bean.protocol.AddCommentRequest;
import collab.fm.server.bean.protocol.CommitRequest;
import collab.fm.server.bean.protocol.CreateModelRequest;
import collab.fm.server.bean.protocol.EditFeatureRequest;
import collab.fm.server.bean.protocol.ListModelRequest;
import collab.fm.server.bean.protocol.LoginRequest;
import collab.fm.server.bean.protocol.RegisterRequest;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.util.exception.JsonConvertException;

public class ProtocolUtil {
	
	static Logger logger = Logger.getLogger(ProtocolUtil.class);
	
	private static Map<String, String> requestHandlerMap = new HashMap<String, String>();
	
	private static Map<String, Class<? extends Request>> requestClassMap = 
		new HashMap<String, Class<? extends Request>>();
	
	private static Map<String, Class<? extends Operation>> opNameClassMap = 
		new HashMap<String, Class<? extends Operation>>();
	
	private static Map<String, Class<? extends RelationshipOperation>> relTypeClassMap = 
		new HashMap<String, Class<? extends RelationshipOperation>>();
	
	static {
		requestHandlerMap.put(Resources.REQ_COMMIT, "doCommitRequest");
		requestHandlerMap.put(Resources.REQ_LOGIN, "doGenericRequest");
		requestHandlerMap.put(Resources.REQ_LOGOUT, "doGenericRequest");
		requestHandlerMap.put(Resources.REQ_REGISTER, "doGenericRequest");
		requestHandlerMap.put(Resources.REQ_LIST_MODEL, "doGenericRequest");
		requestHandlerMap.put(Resources.REQ_CREATE_MODEL, "doGenericRequest");
		requestHandlerMap.put(Resources.REQ_LIST_USER, "doGenericRequest");
		requestHandlerMap.put(Resources.REQ_UPDATE, "doGenericRequest");
		requestHandlerMap.put(Resources.REQ_EDIT, "doGenericRequest");
		requestHandlerMap.put(Resources.REQ_EXIT_MODEL, "doGenericRequest");
		requestHandlerMap.put(Resources.REQ_COMMENT, "doGenericRequest");
		
		requestClassMap.put(Resources.REQ_COMMENT, AddCommentRequest.class);
		requestClassMap.put(Resources.REQ_LOGIN, LoginRequest.class);
		requestClassMap.put(Resources.REQ_REGISTER, RegisterRequest.class);
		requestClassMap.put(Resources.REQ_LIST_MODEL, ListModelRequest.class);
		requestClassMap.put(Resources.REQ_CREATE_MODEL, CreateModelRequest.class);
		requestClassMap.put(Resources.REQ_EDIT, EditFeatureRequest.class);
		
		opNameClassMap.put(Resources.OP_CREATE_RELATIONSHIP, RelationshipOperation.class);
		opNameClassMap.put(Resources.OP_ADD_DES, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_ADD_NAME, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_CREATE_FEATURE, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_SET_OPT, FeatureOperation.class);
		
		relTypeClassMap.put(Resources.BIN_REL_EXCLUDES, BinaryRelationshipOperation.class);
		relTypeClassMap.put(Resources.BIN_REL_REFINES, BinaryRelationshipOperation.class);
		relTypeClassMap.put(Resources.BIN_REL_REQUIRES, BinaryRelationshipOperation.class);
	}
	
	public static String ResponseToJson(Response rsp) throws JsonConvertException {
		if (rsp == null) {
			return null;
		}
		return BeanUtil.beanToJson(rsp);
	}
	
	public static Request jsonToRequest(String json) throws JsonConvertException {
		if (json == null) {
			return null;
		}
			// 1. Convert to the base class Request to get the request name.
			Request abstractReq = BeanUtil.jsonToBean(json, Request.class, null, 
					new String[] {"id", "name", "requesterId", "modelId"});
			if (!abstractReq.valid()) {
				throw new JsonConvertException("Invalid request JSON string.");
				
			}
			// 2. Convert to a concrete request class according to the request name.
			String handlerInfo = requestHandlerMap.get(abstractReq.getName());
			if (handlerInfo == null) {
				throw new JsonConvertException("Invalid request name.");
			}
			try {
			Method handler = ProtocolUtil.class.getDeclaredMethod(handlerInfo, Request.class, String.class);
			return (Request)handler.invoke(null, abstractReq, json);
			} catch (Exception e) {
				throw new JsonConvertException("Unrecognized request class.", e);
			}
	}
	
	@SuppressWarnings("unused")
	private static Request doGenericRequest(Request req, String json) throws JsonConvertException {
			Class<? extends Request> concreteRequestClass = requestClassMap.get(req.getName());
			if (concreteRequestClass == null) {
				concreteRequestClass = Request.class;
			}
			Request result = BeanUtil.jsonToBean(json, concreteRequestClass, null);
			if (!result.valid()) {
				throw new JsonConvertException("Invalid request JSON string. (request='" + concreteRequestClass.toString() + "')");
			}
			result.setId(req.getId());
			result.setName(req.getName());
			result.setRequesterId(req.getRequesterId());
			return result;
	}
	
	@SuppressWarnings("unused")
	private static Request doCommitRequest(Request req, String json) throws JsonConvertException {
			// 1. Convert to CommitRequest with abstract operation
			CommitRequest result = BeanUtil.jsonToBean(json, CommitRequest.class, null);

			if (!result.valid()) {
				throw new JsonConvertException("Invalid CommitRequest JSON String.");
			}
			
			// 2. Get concrete operation class from the abstract operation
			Class<? extends Operation> concreteOpClass = opNameClassMap.get(result.getOperation().getName());
			if (concreteOpClass == null) {
				throw new JsonConvertException("Invalid CommitRequest operation name.");
			}
			logger.debug("Operation class is '" + concreteOpClass.getName() + "'");
			
			// 3. Re-convert to CommitRequest with concrete operation
			OperationHolder holder = BeanUtil.jsonToBean(json, OperationHolder.class, null, 
					new String[] { "operation" });
			Operation concreteOp = BeanUtil.jsonToBean(holder.getOperation(), concreteOpClass, null);
			if (Resources.OP_CREATE_RELATIONSHIP.equals(concreteOp.getName())) {
				Class<? extends RelationshipOperation> relOpClass 
					= relTypeClassMap.get(((RelationshipOperation)concreteOp).getType());
				concreteOp = BeanUtil.jsonToBean(holder.getOperation(), relOpClass, null);
			}
			result.setOperation(concreteOp); 
			
			// 4. Set other infomation from the abstract request.
			result.setId(req.getId());
			result.setName(req.getName());
			result.setRequesterId(req.getRequesterId());
			result.getOperation().setUserid(req.getRequesterId());
			result.getOperation().setModelId(req.getModelId());
			
			logger.debug(result);
			if (!result.valid()) {
				throw new JsonConvertException("Invalid operation JSON string in CommitRequest: " +
						"declared '" + concreteOpClass.getName() + "', but convert failed.");
			}
			return result;
	}
	
	public static class OperationHolder {
		private Object operation;
		public Object getOperation() { return operation; }
		public void setOperation(Object operation) { this.operation = operation; }
	}
	
}
