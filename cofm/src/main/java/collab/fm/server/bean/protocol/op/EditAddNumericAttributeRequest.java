package collab.fm.server.bean.protocol.op;

import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.NumericAttributeType;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DataItemUtil;

public class EditAddNumericAttributeRequest extends EditAddAttributeDefRequest {
	private float min;
	private float max;
	private String unit;
	
	public float getMin() {
		return min;
	}

	public void setMin(float min) {
		this.min = min;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	protected Processor makeDefaultProcessor() {
		return new AddNumericAttrProcessor();
	}
	
	private static class AddNumericAttrProcessor extends AddAttributeProcessor {
		
		@Override
		public boolean checkRequest(Request req) {
			if (!(req instanceof EditAddNumericAttributeRequest)) return false;
			return super.checkRequest(req);
		}
		
		@Override
		protected EditAddAttributeDefRequest.DefaultResponse createResponse(EditAddAttributeDefRequest r) {
			return new DefResponse((EditAddNumericAttributeRequest)r);
		}
		
		@Override
		protected AttributeType createAttribute(EditAddAttributeDefRequest r) {
			NumericAttributeType a = new NumericAttributeType();
			DataItemUtil.setNewDataItemByUserId(a, r.getRequesterId());
			
			a.setAttrName(r.getAttr());
			a.setTypeName(r.getType());
			a.setMultipleSupport(r.getMultiYes());
			a.setEnableGlobalDupValues(r.getAllowDup());
			EditAddNumericAttributeRequest anar = (EditAddNumericAttributeRequest) r;
			a.setMin(anar.getMin());
			a.setMax(anar.getMax());
			a.setUnit(anar.getUnit());
			return a;
		}
	}
	
	public static class DefResponse extends EditAddAttributeDefRequest.DefaultResponse {
		private float min;
		private float max;
		private String unit;
		
		public float getMin() {
			return min;
		}

		public void setMin(float min) {
			this.min = min;
		}

		public float getMax() {
			return max;
		}

		public void setMax(float max) {
			this.max = max;
		}

		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}
		
		public DefResponse(EditAddNumericAttributeRequest r) {
			super(r);
			this.setMax(r.getMax());
			this.setMin(r.getMin());
			this.setUnit(r.getUnit());
		}
	}
}
