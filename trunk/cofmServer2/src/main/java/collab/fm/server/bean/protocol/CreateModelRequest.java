package collab.fm.server.bean.protocol;

import collab.fm.server.bean.entity.Model;
import collab.fm.server.bean.entity.User;
import collab.fm.server.bean.entity.attr.Attribute;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

public class CreateModelRequest extends Request {
	private String modelName;
	private String description;

	@Override
	protected Processor makeDefaultProcessor() {
		return new CreateModelProcessor();
	}
	
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private static class CreateModelProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof CreateModelRequest)) return false;
			CreateModelRequest r = (CreateModelRequest) req;
			return r.getModelName() != null && !(r.getModelName().trim().isEmpty()) &&
				r.getDescription() != null && !(r.getDescription().trim().isEmpty());
		}

		public boolean process(Request req, ResponseGroup rg)
				throws EntityPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid create_model operation.");
			}
			CreateModelRequest cmr = (CreateModelRequest) req;
			CreateModelResponse rsp = new CreateModelResponse(cmr);
			
			// If the model doesn't exist.
			if (DaoUtil.getModelDao().getByName(cmr.getModelName()) == null) {
				Model m = new Model();
				User me = DaoUtil.getUserDao().getById(cmr.getRequesterId(), false);
				if (me == null) {
					throw new InvalidOperationException("Invalid user ID: " + cmr.getRequesterId());
				}
				// Create "name" and "description" attributes for the model.
				Attribute name = new Attribute(cmr.getRequesterId(),
						Resources.ATTR_MODEL_NAME, Attribute.TYPE_STR);
				name.setEnableGlobalDupValues(false);
				
				Attribute des = new Attribute(cmr.getRequesterId(),
						Resources.ATTR_MODEL_DES, Attribute.TYPE_TEXT);
				m.addAttribute(name);
				m.addAttribute(des);
				
				// Add values of "name" and "description" to the model.
				m.voteOrAddValue(Resources.ATTR_MODEL_NAME, cmr.getModelName(), true, cmr.getRequesterId());
				m.voteOrAddValue(Resources.ATTR_MODEL_DES, cmr.getDescription(), true, cmr.getRequesterId());
				
				// Add the user as a contributor of the model.
				me.addModel(m);
				
				// Save
				DaoUtil.getUserDao().save(me);
				m = DaoUtil.getModelDao().save(m);
				
				// Write responses
				rsp.setModelId(m.getId());
				rsp.setName(Resources.RSP_SUCCESS);
			} else {
				// If the model has existed, report as an error.
				rsp.setName(Resources.RSP_ERROR);
				rsp.setMessage("Model has already existed.");
			}
			rg.setBack(rsp);
			return true;
		}
	}
	
	public static class CreateModelResponse extends Response {
		private Long modelId;

		public CreateModelResponse(CreateModelRequest r) {
			super(r);
		}
		
		public Long getModelId() {
			return modelId;
		}

		public void setModelId(Long modelId) {
			this.modelId = modelId;
		}
		
	}
}
