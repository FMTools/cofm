package collab.fm.server.bean.protocol;

import collab.fm.server.processor.Processor;

public class FocusOnEntityRequest extends Request {
	private Long modelId;
	private Long entityId;
	
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

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}


	private static class FocusOnFeatureProcessor extends Request.SimpleBroadcastingProcessor {
		
		@Override
		public boolean checkRequest(Request req) {
			if (!(req instanceof FocusOnEntityRequest)) return false;
			return true;
		}
		
		@Override
		protected Response fillResponse(Request req) {
			return new FocusOnFeatureResponse((FocusOnEntityRequest)req);
		}
	}
	
	public static class FocusOnFeatureResponse extends Response {
		private Long entityId;
		private Long modelId;
		
		public FocusOnFeatureResponse(FocusOnEntityRequest r) {
			super(r);
			this.setEntityId(r.getEntityId());
			this.setModelId(r.getModelId());
		}
		
		public Long getModelId() {
			return modelId;
		}

		public void setModelId(Long modelId) {
			this.modelId = modelId;
		}

		public Long getEntityId() {
			return entityId;
		}

		public void setEntityId(Long entityId) {
			this.entityId = entityId;
		}

	}
	
}
