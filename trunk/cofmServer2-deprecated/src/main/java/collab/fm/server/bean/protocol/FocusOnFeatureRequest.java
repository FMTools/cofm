package collab.fm.server.bean.protocol;

import collab.fm.server.processor.Processor;

public class FocusOnFeatureRequest extends Request {
	private Long modelId;
	private Long featureId;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new FocusOnFeatureProcessor();
	}
	
	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public Long getFeatureId() {
		return featureId;
	}

	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
	}
	
	private static class FocusOnFeatureProcessor extends Request.SimpleBroadcastingProcessor {
		
		@Override
		public boolean checkRequest(Request req) {
			if (!(req instanceof FocusOnFeatureRequest)) return false;
			return true;
		}
		
		@Override
		protected Response fillResponse(Request req) {
			return new FocusOnFeatureResponse((FocusOnFeatureRequest)req);
		}
	}
	
	public static class FocusOnFeatureResponse extends Response {
		private Long featureId;
		private Long modelId;
		
		public FocusOnFeatureResponse(FocusOnFeatureRequest r) {
			super(r);
			this.setFeatureId(r.getFeatureId());
			this.setModelId(r.getModelId());
		}
		
		public Long getModelId() {
			return modelId;
		}

		public void setModelId(Long modelId) {
			this.modelId = modelId;
		}

		public Long getFeatureId() {
			return featureId;
		}

		public void setFeatureId(Long featureId) {
			this.featureId = featureId;
		}
	}
	
}
