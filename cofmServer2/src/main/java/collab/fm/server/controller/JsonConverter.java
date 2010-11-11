package collab.fm.server.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import collab.fm.server.bean.protocol.AddCommentRequest;
import collab.fm.server.bean.protocol.CreateModelRequest;
import collab.fm.server.bean.protocol.ExitModelRequest;
import collab.fm.server.bean.protocol.FocusOnFeatureRequest;
import collab.fm.server.bean.protocol.ListModelRequest;
import collab.fm.server.bean.protocol.ListUserRequest;
import collab.fm.server.bean.protocol.LoginRequest;
import collab.fm.server.bean.protocol.RegisterRequest;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.UpdateRequest;
import collab.fm.server.bean.protocol.op.EditAddAttributeDefRequest;
import collab.fm.server.bean.protocol.op.EditAddEnumAttributeDefRequest;
import collab.fm.server.bean.protocol.op.EditAddNumericAttributeRequest;
import collab.fm.server.bean.protocol.op.VoteAddBinRelationRequest;
import collab.fm.server.bean.protocol.op.VoteAddEntityRequest;
import collab.fm.server.bean.protocol.op.VoteAddValueRequest;
import collab.fm.server.util.JsonUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.JsonConvertException;

public class JsonConverter {
	static Logger logger = Logger.getLogger(JsonConverter.class);
	
	private static Map<String, Class<? extends Request>> requestClassMap = 
		new HashMap<String, Class<? extends Request>>();
	
	static {
		requestClassMap.put(Resources.REQ_COMMENT, AddCommentRequest.class);
		requestClassMap.put(Resources.REQ_LOGIN, LoginRequest.class);
		requestClassMap.put(Resources.REQ_REGISTER, RegisterRequest.class);
		requestClassMap.put(Resources.REQ_LIST_MODEL, ListModelRequest.class);
		requestClassMap.put(Resources.REQ_CREATE_MODEL, CreateModelRequest.class);
		requestClassMap.put(Resources.REQ_FOCUS_ON_FEATURE, FocusOnFeatureRequest.class);
		requestClassMap.put(Resources.REQ_LIST_USER, ListUserRequest.class);
		requestClassMap.put(Resources.REQ_EXIT_MODEL, ExitModelRequest.class);
		requestClassMap.put(Resources.REQ_UPDATE, UpdateRequest.class);
		
		requestClassMap.put(Resources.REQ_VA_ATTR_ENUM, EditAddEnumAttributeDefRequest.class);
		requestClassMap.put(Resources.REQ_VA_ATTR_NUMBER, EditAddNumericAttributeRequest.class);
		requestClassMap.put(Resources.REQ_VA_ATTR_STR, EditAddAttributeDefRequest.class);
		requestClassMap.put(Resources.REQ_VA_FEATURE, VoteAddEntityRequest.class);
		requestClassMap.put(Resources.REQ_VA_RELATION_BIN, VoteAddBinRelationRequest.class);
		requestClassMap.put(Resources.REQ_VA_VALUE, VoteAddValueRequest.class);
		
	}
	
	public static String responseToJson(Response rsp) throws JsonConvertException {
		if (rsp == null) {
			return null;
		}
		return JsonUtil.beanToJson(rsp);
	}
	
	public static Request jsonToRequest(String json) throws JsonConvertException {
		if (json == null) {
			return null;
		}
		// 1. Convert to the base class Request to get the request name.
		Request abstractReq = JsonUtil.jsonToBean(json, Request.class, null, 
				new String[] {"name"});
				//new String[] {"id", "name", "requesterId", "modelId"});
		if (!abstractReq.valid()) {
			throw new JsonConvertException("Invalid request name.");
		}
		
		// 2. Convert to a concrete request class according to the request name.
		Class<? extends Request> reqClz = requestClassMap.get(abstractReq.getName());
		if (reqClz == null) {
			reqClz = Request.class;
		}
		return JsonUtil.jsonToBean(json, reqClz, null);
	}	
}
