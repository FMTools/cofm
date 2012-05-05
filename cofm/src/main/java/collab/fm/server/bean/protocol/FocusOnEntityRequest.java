package collab.fm.server.bean.protocol;

import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

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
			FocusOnEntityRequest r = (FocusOnEntityRequest) req;
			return r.getModelId() != null && r.getEntityId() != null; 
		}
		
		@Override
		protected Response fillResponse(Request req) {
			return new FocusOnFeatureResponse((FocusOnEntityRequest)req);
		}
		
		@Override
		protected void recordRequest(Request req) throws ItemPersistenceException, StaleDataException, InvalidOperationException {
			FocusOnEntityRequest r = (FocusOnEntityRequest) req;
			Entity en = DaoUtil.getEntityDao().getById(r.getEntityId(), false);
			if (en == null) {
				throw new InvalidOperationException("Invalid entity ID.");
			}
			en.getVote().view(r.getRequesterId());
			DaoUtil.getEntityDao().save(en);
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
