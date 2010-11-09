package collab.fm.server.bean.protocol.op;

import java.util.List;

import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.EnumAttributeType;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.processor.Processor;

public class EditAddEnumAttributeRequest extends EditAddAttributeDefRequest {
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
			if (!(req instanceof EditAddEnumAttributeRequest)) return false;
			EditAddEnumAttributeRequest r = (EditAddEnumAttributeRequest) req;
			if (r.getVlist() == null || r.getVlist().size() <= 0) return false;
			return super.checkRequest(req);
		}
		
		@Override
		protected EditAddAttributeDefRequest.DefaultResponse createResponse(EditAddAttributeDefRequest r) {
			return new DefResponse((EditAddEnumAttributeRequest)r);
		}
		
		@Override
		protected AttributeType createAttribute(EditAddAttributeDefRequest r) {
			EnumAttributeType a = new EnumAttributeType();
			a.setCreator(r.getRequesterId());
			a.setMultipleSupport(r.getMultiYes());
			a.setEnableGlobalDupValues(r.getAllowDup());
			a.setValidValues(((EditAddEnumAttributeRequest)r).getVlist());
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
		
		public DefResponse(EditAddEnumAttributeRequest r) {
			super(r);
			this.setVlist(r.getVlist());
		}
	}
}
