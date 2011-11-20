package collab.fm.server.bean.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import collab.fm.server.bean.persist.DataItem;
import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.PersonalView;
import collab.fm.server.bean.persist.Preference;
import collab.fm.server.bean.persist.User;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class ChangePersonalViewRequest extends Request {

	private Long modelId;
	private Long pvId;

	@Override
	protected Processor makeDefaultProcessor() {
		return new ChangePersonalViewProcessor();
	}
	
	public void setPvId(Long pvId) {
		this.pvId = pvId;
	}

	public Long getPvId() {
		return pvId;
	}
	
	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}
	
	private static class ChangePersonalViewProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof ChangePersonalViewRequest)) return false;
			return ((ChangePersonalViewRequest) req).getPvId() != null;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid change_pv operation.");
			}
			
			ChangePersonalViewRequest r = (ChangePersonalViewRequest) req;
			
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), false);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}
			
			PersonalView pv = DaoUtil.getPersonalViewDao().getById(r.getPvId(), false);
			if (pv == null) {
				throw new InvalidOperationException("Invalid personal view ID: " + r.getPvId());
			}
			
			User user = DaoUtil.getUserDao().getById(r.getRequesterId(), false);
			if (user == null) {
				throw new InvalidOperationException("Invalid user ID: " + r.getRequesterId());
			}
			
			Preference pref = new Preference();
			pref.setModel(m);
			pref.setContent(r.getPvId().toString());
			user.addPreference(pref);
			
			DaoUtil.getUserDao().save(user);
			
			ChangePersonalViewResponse rsp = new ChangePersonalViewResponse(r);
			rsp.setEntities(extractId(pv.getEntities()));
			rsp.setBinrels(extractId(pv.getRelations()));
			rsp.setValues(extractId(pv.getValues()));
			
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			return true;
		}

		private List<Long> extractId(Set<? extends DataItem> idSet) {
			List<Long> result = new ArrayList<Long>();
			for (DataItem d: idSet) {
				result.add(d.getId());
			}
			if (result.size() > 0) {
				return result;
			}
			return null;
		}
		
	}
	
	public static class ChangePersonalViewResponse extends Response {
		
		private Long pvId;
		private List<Long> entities;
		private List<Long> binrels;
		private List<Long> values;
		
		public ChangePersonalViewResponse(ChangePersonalViewRequest r) {
			super(r);
			this.setPvId(r.getPvId());
		}

		public void setPvId(Long pvId) {
			this.pvId = pvId;
		}

		public Long getPvId() {
			return pvId;
		}

		public void setEntities(List<Long> entities) {
			this.entities = entities;
		}

		public List<Long> getEntities() {
			return entities;
		}

		public void setBinrels(List<Long> binrels) {
			this.binrels = binrels;
		}

		public List<Long> getBinrels() {
			return binrels;
		}

		public void setValues(List<Long> values) {
			this.values = values;
		}

		public List<Long> getValues() {
			return values;
		}
	}
}
