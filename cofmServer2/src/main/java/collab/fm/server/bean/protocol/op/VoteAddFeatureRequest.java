package collab.fm.server.bean.protocol.op;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.persist.Feature;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.Relationship;
import collab.fm.server.bean.persist.entity.AttributeType;
import collab.fm.server.bean.persist.entity.EnumAttributeType;
import collab.fm.server.bean.protocol.Request;
import collab.fm.server.bean.protocol.Response;
import collab.fm.server.bean.protocol.ResponseGroup;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

public class VoteAddFeatureRequest extends Request {
	// Which feature model is the feature in?
	private Long modelId;
	
	// For a vote request, featureId > 0; for an add request, featureId == null
	private Long featureId;
	
	// "yes" is ignored for an add request
	private Boolean yes;
	
	// For a vote request, featureName == null; for an add request, featureName == null or String
	private String featureName;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new VoteAddFeatureProcessor();
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

	public Boolean getYes() {
		return yes;
	}

	public void setYes(Boolean yes) {
		this.yes = yes;
	}

	public String getFeatureName() {
		return featureName;
	}

	public void setFeatureName(String featureName) {
		this.featureName = featureName;
	}

	private static class VoteAddFeatureProcessor implements Processor {

		public boolean process(Request req, ResponseGroup rg) 
		throws InvalidOperationException, EntityPersistenceException, StaleDataException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid vote_or_add_feature operation.");
			}
			VoteAddFeatureRequest vafr = (VoteAddFeatureRequest) req;
			DefaultResponse rsp = new DefaultResponse((VoteAddFeatureRequest)req);
			
			// If this is a creation
			if (vafr.getFeatureId() == null) {
				Model model = DaoUtil.getModelDao().getById(vafr.getModelId(), false);
				if (model == null) {
					throw new InvalidOperationException("Invalid feature model ID: " + vafr.getModelId());
				}
				// Check if a feature with the same name has already existed.
				Feature f = null;
				Feature sameNamed = DaoUtil.getFeatureDao().getByName(vafr.getModelId(), vafr.getFeatureName());
				if (sameNamed != null) {
					rsp.setExist(new Boolean(true));
					f = sameNamed;
				} else {
					rsp.setExist(new Boolean(false));
					// Create the "name", "description" and "optionality" attributes for the feature.
					f = new Feature(vafr.getRequesterId());
					
					AttributeType fname = new AttributeType(vafr.getRequesterId(), 
							Resources.ATTR_FEATURE_NAME, AttributeType.TYPE_STR);
					fname.setEnableGlobalDupValues(false);
					
					AttributeType fdes = new AttributeType(vafr.getRequesterId(), 
							Resources.ATTR_FEATURE_DES, AttributeType.TYPE_TEXT);
					
					EnumAttributeType fopt = new EnumAttributeType(vafr.getRequesterId(),
							Resources.ATTR_FEATURE_OPT);
					fopt.addValidValue(Resources.VAL_OPT_MANDATORY);
					fopt.addValidValue(Resources.VAL_OPT_OPTIONAL);
					
					f.addAttribute(fname);
					f.addAttribute(fdes);
					f.addAttribute(fopt);
					
					model.addFeature(f);
				}
				
				// Add the initial value for "name" attribute
				f.voteOrAddValue(Resources.ATTR_FEATURE_NAME,
						vafr.getFeatureName(), true, vafr.getRequesterId());
				f.vote(true, vafr.getRequesterId());
				
				// Save the feature and set featureId in the response.
				f = DaoUtil.getFeatureDao().save(f);
				rsp.setFeatureId(f.getId());
				// Save the model
				if (rsp.getExist().booleanValue() == false) {
					DaoUtil.getModelDao().save(model);
				}
				
			} else {
				// Otherwise this is a vote on an existed feature.
				rsp.setExist(new Boolean(true));
				Feature f = DaoUtil.getFeatureDao().getById(vafr.getFeatureId(), false);
				if (f == null) {
					throw new InvalidOperationException("Invalid feature ID: " + vafr.getFeatureId());
				}
				
				if (vafr.getYes().booleanValue() == false) {
					// Set the inferred vote in response
					List<Long> targets = new ArrayList<Long>();
					for (Relationship r: f.getRels()) {
						targets.add(r.getId());
					}
					if (targets.size() > 0) {
						rsp.setInferVotes(targets);
					}
				}
				
				if (f.vote(vafr.getYes().booleanValue(), vafr.getRequesterId())) {
					DaoUtil.getFeatureDao().save(f);
				} else {
					DaoUtil.getFeatureDao().delete(f);
				}
				
				// For voting operations, we don't need the featureName fields in the response, so we
				// set it to null (this could shorten the length of JSON string)
				rsp.setFeatureName(null);
			}
			
			// Write the responses (back and broadcast)
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			DefaultResponse rsp2 = (DefaultResponse) rsp.clone();
			rsp2.setName(Resources.RSP_FORWARD);
			rg.setBroadcast(rsp2);
			
			return true;
		}
		
		public boolean checkRequest(Request req) {
			if (!(req instanceof VoteAddFeatureRequest)) return false;
			VoteAddFeatureRequest r = (VoteAddFeatureRequest) req;
			if (r.getModelId() == null || r.getRequesterId() == null) return false;
			if (r.getFeatureId() == null) {
				return r.getFeatureName() != null;
			}
			return r.getYes() != null;
		}
	}
	
	public static class DefaultResponse extends Response {
		
		// exist == true if this is a voting.
		private Boolean exist;
		
		private Long modelId;
		private Long featureId;
		private Boolean yes;
		private String featureName;
		
		// inference votes on relationships (if any)
		private List<Long> inferVotes;
		
		public DefaultResponse(VoteAddFeatureRequest r) {
			super(r);
			this.setModelId(r.getModelId());
			this.setFeatureId(r.getFeatureId());
			this.setYes(r.getYes());
			this.setFeatureName(r.getFeatureName());
		}
		
		public Boolean getExist() {
			return exist;
		}

		public void setExist(Boolean exist) {
			this.exist = exist;
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

		public Boolean getYes() {
			return yes;
		}

		public void setYes(Boolean yes) {
			this.yes = yes;
		}

		public String getFeatureName() {
			return featureName;
		}

		public void setFeatureName(String featureName) {
			this.featureName = featureName;
		}

		public List<Long> getInferVotes() {
			return inferVotes;
		}

		public void setInferVotes(List<Long> inferVotes) {
			this.inferVotes = inferVotes;
		}
		
	}
}
