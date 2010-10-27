package collab.fm.server.bean.protocol.op;

import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.NumericAttributeType;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.processor.Processor;

public class AddNumericAttributeRequest extends AddAttributeRequest {
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
			if (!(req instanceof AddNumericAttributeRequest)) return false;
			AddNumericAttributeRequest r = (AddNumericAttributeRequest) req;
			if (r.getUnit() == null) return false;
			return super.checkRequest(req);
		}
		
		@Override
		protected AddAttributeRequest.DefaultResponse createResponse(AddAttributeRequest r) {
			return new DefResponse((AddNumericAttributeRequest)r);
		}
		
		@Override
		protected AttributeType createAttribute(AddAttributeRequest r) {
			NumericAttributeType a = new NumericAttributeType();
			a.setCreator(r.getRequesterId());
			a.setMultipleSupport(r.getMultiYes());
			a.setEnableGlobalDupValues(r.getAllowDup());
			AddNumericAttributeRequest anar = (AddNumericAttributeRequest) r;
			a.setMin(anar.getMin());
			a.setMax(anar.getMax());
			a.setUnit(anar.getUnit());
			return a;
		}
	}
	
	public static class DefResponse extends AddAttributeRequest.DefaultResponse {
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
		
		public DefResponse(AddNumericAttributeRequest r) {
			super(r);
			this.setMax(r.getMax());
			this.setMin(r.getMin());
			this.setUnit(r.getUnit());
		}
	}
}
