package collab.fm.server.bean.protocol;

import collab.fm.server.bean.persist.Comment;
import collab.fm.server.bean.persist.Feature;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.EntityUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

public class AddCommentRequest extends Request {
	private Long featureId;
	private String content;
	
	@Override 
	protected Processor makeDefaultProcessor() {
		return new AddCommentProcessor();
	}
	
	public Long getFeatureId() {
		return featureId;
	}
	public void setFeatureId(Long featureId) {
		this.featureId = featureId;
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
			return r.getFeatureId() != null &&
				r.getContent() != null && 
				!(r.getContent().trim().isEmpty());
		}

		public boolean process(Request req, ResponseGroup rg)
				throws EntityPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid add_comment operation.");
			}
			AddCommentRequest acr = (AddCommentRequest) req;
			AddCommentResponse rsp = new AddCommentResponse(acr);
			
			Feature f = DaoUtil.getFeatureDao().getById(acr.getFeatureId(), false);
			if (f == null) {
				throw new InvalidOperationException("Invalid feature ID: " + acr.getFeatureId());
			}
			
			Comment c = new Comment(acr.getRequesterId());
			c.setContent(acr.getContent());
			f.addComment(c);
			DaoUtil.getFeatureDao().save(f);
			
			// Set the date/time in response
			rsp.setDateTime(EntityUtil.formatDate(c.getCreateTime()));
			
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
		
		private Long featureId;
		private String content;
		private String dateTime;
		
		public AddCommentResponse(AddCommentRequest r) {
			super(r);
			this.setContent(r.getContent());
			this.setFeatureId(r.getFeatureId());
		}
		
		public Long getFeatureId() {
			return featureId;
		}
		public void setFeatureId(Long featureId) {
			this.featureId = featureId;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getDateTime() {
			return dateTime;
		}
		public void setDateTime(String dateTime) {
			this.dateTime = dateTime;
		}
	}
	
}
