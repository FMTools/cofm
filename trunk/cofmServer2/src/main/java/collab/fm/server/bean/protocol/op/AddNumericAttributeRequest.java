package collab.fm.server.bean.protocol.op;

import java.util.List;

import collab.fm.server.bean.entity.attr.Attribute;
import collab.fm.server.bean.entity.attr.EnumAttribute;
import collab.fm.server.bean.entity.attr.NumericAttribute;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.op.AddAttributeRequest.AddAttributeProcessor;
import collab.fm.server.bean.protocol.op.AddEnumAttributeRequest.DefResponse;
import collab.fm.server.processor.Processor;

public class AddNumericAttributeRequest extends AddAttributeRequest {
	private Double min;
	private Double max;
	private String unit;
	
	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
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
		protected Attribute createAttribute(AddAttributeRequest r) {
			NumericAttribute a = new NumericAttribute(r.getRequesterId(), r.getAttr());
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
		private Double min;
		private Double max;
		private String unit;
		
		public Double getMin() {
			return min;
		}

		public void setMin(Double min) {
			this.min = min;
		}

		public Double getMax() {
			return max;
		}

		public void setMax(Double max) {
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
