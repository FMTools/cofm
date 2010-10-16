package collab.fm.server.bean.protocol;

import collab.fm.server.processor.Processor;

public class ExitModelRequest extends Request {
	private Long modelId;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new ExitModelProcessor();
	}
	
	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	private static class ExitModelProcessor extends Request.SimpleBroadcastingProcessor {
		@Override
		protected Response fillResponse(Request req) {
			return new ExitModelResponse((ExitModelRequest)req);
		}
		
		@Override
		public boolean checkRequest(Request req) {
			if (!(req instanceof ExitModelRequest)) return false;
			return true;
		}
	}
	
	public static class ExitModelResponse extends Response {

		private Long modelId;

		public ExitModelResponse(ExitModelRequest r) {
			super(r);
			this.setModelId(r.getModelId());
		}
		
		public Long getModelId() {
			return modelId;
		}

		public void setModelId(Long modelId) {
			this.modelId = modelId;
		}
	}
}
