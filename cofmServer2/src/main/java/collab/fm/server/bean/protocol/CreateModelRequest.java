package collab.fm.server.bean.protocol;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.User;
import collab.fm.server.bean.persist.entity.Attribute;
import collab.fm.server.bean.persist.entity.EnumAttribute;
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
				
				m.setName(cmr.getModelName());
				m.setDescription(cmr.getDescription());
				
				// Add the default feature attribute set to the model.
				Attribute fname = new Attribute(cmr.getRequesterId(), 
						Resources.ATTR_FEATURE_NAME, Attribute.TYPE_STR);
				fname.setEnableGlobalDupValues(false);
				
				Attribute fdes = new Attribute(cmr.getRequesterId(), 
						Resources.ATTR_FEATURE_DES, Attribute.TYPE_TEXT);
				
				EnumAttribute fopt = new EnumAttribute(cmr.getRequesterId(),
						Resources.ATTR_FEATURE_OPT);
				fopt.addValidValue(Resources.VAL_OPT_MANDATORY);
				fopt.addValidValue(Resources.VAL_OPT_OPTIONAL);
				
				m.addAttributeToFeatures(fname);
				m.addAttributeToFeatures(fdes);
				m.addAttributeToFeatures(fopt);
				
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
