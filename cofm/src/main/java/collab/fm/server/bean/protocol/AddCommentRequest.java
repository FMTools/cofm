package collab.fm.server.bean.protocol;

import collab.fm.server.bean.persist.Comment;
import collab.fm.server.bean.persist.entity.Entity;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.DataItemUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class AddCommentRequest extends Request {
	private Long entityId;
	private String content;
	
	@Override 
	protected Processor makeDefaultProcessor() {
		return new AddCommentProcessor();
	}
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	private static class AddCommentProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof AddCommentRequest)) return false;
			AddCommentRequest r = (AddCommentRequest) req;
			return r.getEntityId() != null &&
				r.getContent() != null && 
				!(r.getContent().trim().isEmpty());
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid add_comment operation.");
			}
			AddCommentRequest acr = (AddCommentRequest) req;
			AddCommentResponse rsp = new AddCommentResponse(acr);
			
			Entity f = DaoUtil.getEntityDao().getById(acr.getEntityId(), false);
			if (f == null) {
				throw new InvalidOperationException("Invalid feature ID: " + acr.getEntityId());
			}
			
			Comment c = new Comment();
			DataItemUtil.setNewDataItemByUserId(c, acr.getRequesterId());
			c.setContent(acr.getContent());
			f.addComment(c);
			f.getVote().view(acr.getEntityId());
			DaoUtil.getEntityDao().save(f);
			
			// Set the date/time in response
			rsp.setExecTime(DataItemUtil.formatDate(c.getCreateTime()));
			
			// Write responses
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			AddCommentResponse rsp2 = (AddCommentResponse) rsp.clone();
			rsp2.setName(Resources.RSP_FORWARD);		
			rg.setBroadcast(rsp2);
			return true;
		}
		
	}
	
	public static class AddCommentResponse extends Response {
		
		private Long entityId;
		private String content;
		
		public AddCommentResponse(AddCommentRequest r) {
			super(r);
			this.setContent(r.getContent());
			this.setEntityId(r.getEntityId());
		}
		
		public Long getEntityId() {
			return entityId;
		}

		public void setEntityId(Long entityId) {
			this.entityId = entityId;
		}

		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
	}
	
}
