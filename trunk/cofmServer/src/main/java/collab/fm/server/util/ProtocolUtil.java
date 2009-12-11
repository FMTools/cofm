package collab.fm.server.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import collab.fm.server.bean.operation.BinaryRelationshipOperation;
import collab.fm.server.bean.operation.FeatureOperation;
import collab.fm.server.bean.operation.Operation;
import collab.fm.server.bean.protocol.CommitRequest;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.util.exception.ProtocolInterpretException;

public class ProtocolUtil {
	
	static Logger logger = Logger.getLogger(ProtocolUtil.class);
	
	private static Map<String, String> requestHandlerMap = new HashMap<String, String>();
	
	private static Map<String, Class<? extends Operation>> opNameClassMap = 
		new HashMap<String, Class<? extends Operation>>();
	
	static {
		requestHandlerMap.put(Resources.REQ_COMMIT, "doCommitRequest");

		opNameClassMap.put(Resources.OP_CREATE_BINARY_RELATIONSHIP, BinaryRelationshipOperation.class);
		
		opNameClassMap.put(Resources.OP_ADD_DES, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_ADD_NAME, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_CREATE_FEATURE, FeatureOperation.class);
		opNameClassMap.put(Resources.OP_SET_OPT, FeatureOperation.class);
	}
	
	public static String ResponseToJson(Response rsp) throws ProtocolInterpretException {
		if (rsp == null) {
			return null;
		}
		try {
			return BeanUtil.beanToJson(rsp);
		} catch (Exception e) {
			logger.warn("Couldn't convert response to JSON.", e);
			throw new ProtocolInterpretException(e);
		}
	}
	
	public static Request jsonToRequest(String json) throws ProtocolInterpretException {
		if (json == null) {
			return null;
		}
		try {
			// 1. Convert to the base class Request to get the request name.
			Request abstractReq = BeanUtil.jsonToBean(json, Request.class, null, 
					new String[] {"id", "name", "requesterId"});
			if (!abstractReq.valid()) {
				throw new ProtocolInterpretException("Invalid request JSON string.");
				
			}
			// 2. Convert to a concrete request class according to the request name.
			String handlerName = requestHandlerMap.get(abstractReq.getName());
			if (handlerName == null) {
				throw new ProtocolInterpretException("Invalid request name.");
			}
			Method handler = ProtocolUtil.class.getDeclaredMethod(handlerName, Request.class, String.class);
			return (Request)handler.invoke(null, abstractReq, json);
		} catch (Exception e) {
			logger.warn("Json to Request failed.", e);
			throw new ProtocolInterpretException(e);
		}		
	}
	
	@SuppressWarnings("unused")
	private static Request doCommitRequest(Request req, String json) throws ProtocolInterpretException {
		try {
			// 1. Convert to CommitRequest with abstract operation
			CommitRequest result = BeanUtil.jsonToBean(json, CommitRequest.class, null);

			if (!result.valid()) {
				throw new ProtocolInterpretException("Invalid CommitRequest JSON String.");
			}
			
			// 2. Get concrete operation class from the abstract operation
			Class<? extends Operation> concreteOpClass = opNameClassMap.get(result.getOperation().getName());
			if (concreteOpClass == null) {
				throw new ProtocolInterpretException("Invalid CommitRequest operation name.");
			}
			logger.debug("Operation class is '" + concreteOpClass.getName() + "'");
			
			// 3. Re-convert to CommitRequest with concrete operation
			OperationHolder holder = BeanUtil.jsonToBean(json, OperationHolder.class, null, 
					new String[] { "operation" });
			result.setOperation(BeanUtil.jsonToBean(holder.getOperation(), concreteOpClass, null)); 
			
			// 4. Set other infomation from the abstract request.
			result.setId(req.getId());
			result.setName(req.getName());
			result.setRequesterId(req.getRequesterId());
			result.getOperation().setUserid(req.getRequesterId());
			
			if (!result.valid()) {
				throw new ProtocolInterpretException("Invalid operation JSON string in CommitRequest: " +
						"declared '" + concreteOpClass.getName() + "', but convert failed.");
			}
			return result;
		} catch (Exception e) {
			logger.warn("Json to CommitRequest failed.", e);
			throw new ProtocolInterpretException(e);
		}
	}
	
	public static class OperationHolder {
		private Object operation;
		public Object getOperation() { return operation; }
		public void setOperation(Object operation) { this.operation = operation; }
	}
	
}
