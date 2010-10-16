package collab.fm.server.bean.protocol.op;

import java.util.List;

import collab.fm.server.bean.entity.attr.Attribute;
import collab.fm.server.bean.entity.attr.EnumAttribute;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.processor.Processor;

public class AddEnumAttributeRequest extends AddAttributeRequest {
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
			if (!(req instanceof AddEnumAttributeRequest)) return false;
			AddEnumAttributeRequest r = (AddEnumAttributeRequest) req;
			if (r.getVlist() == null || r.getVlist().size() <= 0) return false;
			return super.checkRequest(req);
		}
		
		@Override
		protected AddAttributeRequest.DefaultResponse createResponse(AddAttributeRequest r) {
			return new DefResponse((AddEnumAttributeRequest)r);
		}
		
		@Override
		protected Attribute createAttribute(AddAttributeRequest r) {
			EnumAttribute a = new EnumAttribute(r.getRequesterId(), r.getAttr());
			a.setMultipleSupport(r.getMultiYes());
			a.setEnableGlobalDupValues(r.getAllowDup());
			a.setValidValues(((AddEnumAttributeRequest)r).getVlist());
			return a;
		}
	}
	
	public static class DefResponse extends AddAttributeRequest.DefaultResponse {
		private List<String> vlist;

		public List<String> getVlist() {
			return vlist;
		}

		public void setVlist(List<String> vlist) {
			this.vlist = vlist;
		}
		
		public DefResponse(AddEnumAttributeRequest r) {
			super(r);
			this.setVlist(r.getVlist());
		}
	}
}
