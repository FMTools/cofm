package collab.fm.server.bean.protocol.op;

import java.util.List;

import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.EnumAttributeType;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DataItemUtil;

public class EditAddEnumAttributeDefRequest extends EditAddAttributeDefRequest {
	private List<String> vlist;

	public List<String> getVlist() {
		return vlist;
	}

	public void setVlist(List<String> vlist) {
		this.vlist = vlist;
	}
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new AddEnumAttrProcessor();
	}
	
	private static class AddEnumAttrProcessor extends AddAttributeProcessor {
		
		@Override
		public boolean checkRequest(Request req) {
			if (!(req instanceof EditAddEnumAttributeDefRequest)) return false;
			EditAddEnumAttributeDefRequest r = (EditAddEnumAttributeDefRequest) req;
			if (r.getAttrId() == null &&
					(r.getVlist() == null || r.getVlist().size() <= 0)) {
				return false;
			}
			return super.checkRequest(req);
		}
		
		@Override
		protected EditAddAttributeDefRequest.DefaultResponse createResponse(EditAddAttributeDefRequest r) {
			return new DefResponse((EditAddEnumAttributeDefRequest)r);
		}
		
		@Override
		protected AttributeType createAttribute(EditAddAttributeDefRequest r) {
			EnumAttributeType a = new EnumAttributeType();
			DataItemUtil.setNewDataItemByUserId(a, r.getRequesterId());
			
			a.setAttrName(r.getAttr());
			a.setTypeName(r.getType());
			a.setMultipleSupport(r.getMultiYes());
			a.setEnableGlobalDupValues(r.getAllowDup());
			a.setValidValues(((EditAddEnumAttributeDefRequest)r).getVlist());
			return a;
		}
	}
	
	public static class DefResponse extends EditAddAttributeDefRequest.DefaultResponse {
		private List<String> vlist;

		public List<String> getVlist() {
			return vlist;
		}

		public void setVlist(List<String> vlist) {
			this.vlist = vlist;
		}
		
		public DefResponse(EditAddEnumAttributeDefRequest r) {
			super(r);
			this.setVlist(r.getVlist());
		}
	}
}
