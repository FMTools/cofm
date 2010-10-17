package collab.fm.server.bean.protocol.op;

import collab.fm.server.bean.persist.Feature;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.EntityUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

// Vote or add value to a Model or a Feature
public class VoteAddValueRequest extends Request {

	private Long modelId;
	private Long featureId;
	private String attr;
	private String val;
	private Boolean yes;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new VoteAddValueProcessor();
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
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getVal() {
		return val;
	}
	public void setVal(String val) {
		this.val = val;
	}
	public Boolean getYes() {
		return yes;
	}
	public void setYes(Boolean yes) {
		this.yes = yes;
	}
	
	private static class VoteAddValueProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof VoteAddValueRequest)) return false;
			VoteAddValueRequest r = (VoteAddValueRequest) req;
			return r.getModelId() != null && r.getAttr() != null && r.getVal() != null;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws EntityPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid vote_or_add_value operation");
			}
			VoteAddValueRequest r = (VoteAddValueRequest) req;
			DefaultResponse rsp = new DefaultResponse(r);
			
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), false);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}

			Feature target = DaoUtil.getFeatureDao().getById(r.getFeatureId(), true);
			if (target == null) {
				throw new InvalidOperationException("Invalid feature ID: "
						+ r.getFeatureId());
			}
			
			AttributeType a = target.getAttribute(r.getAttr());
			if (a == null) {
				// We can get the attribute instance from Model.featureAttrs
				a = m.getFeatureAttrs().get(r.getAttr());
				if (a == null) {
					throw new InvalidOperationException("Unknown attribute of features: " + r.getAttr());
				}
			}
			
			// If the attribute is NOT allow to global replicated, we should check if the same value existed
			if (!a.isEnableGlobalDupValues()) {
				Feature f = DaoUtil.getFeatureDao().getByAttrValue(r.getModelId(), r.getAttr(), r.getVal());
				if (f != null) { // if the same value exists, then the target must be this object.
					target = f;
					rsp.setFeatureId(f.getId());
				}
			}
			
			// If the target is a feature, we should make sure the attribute exists
			if (target.getAttribute(r.getAttr()) == null) {
				AttributeType a2 = EntityUtil.cloneAttribute(a);
				a2.setCreator(r.getRequesterId());
				target.addAttribute(a2);
			}
			boolean isValidValue = target.voteOrAddValue(r.getAttr(), r.getVal(), r.getYes(), r.getRequesterId());
			
			if (!isValidValue) {
				req.setLastError("Invalid value: " + r.getVal());
				return false;
			}
			
			DaoUtil.getFeatureDao().save(target);
			
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = (DefaultResponse) rsp.clone();
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}
		
	}
	
	public static class DefaultResponse extends Response {
		private Long modelId;
		private Long featureId;
		private String attr;
		private String val;
		private Boolean yes;
		
		public DefaultResponse(VoteAddValueRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setFeatureId(r.getFeatureId());
			this.setAttr(r.getAttr());
			this.setVal(r.getVal());
			this.setYes(r.getYes());
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
		public String getAttr() {
			return attr;
		}
		public void setAttr(String attr) {
			this.attr = attr;
		}
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		public Boolean getYes() {
			return yes;
		}
		public void setYes(Boolean yes) {
			this.yes = yes;
		}
	}
}
