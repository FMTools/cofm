package collab.fm.server.bean.protocol;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.PersonalView;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.DataItemUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.ItemPersistenceException;
import collab.fm.server.util.exception.StaleDataException;

public class CreatePersonalViewRequest extends Request {

	private Long modelId;
	private String pvName;
	private String pvDes;
	
	@Override
	protected Processor makeDefaultProcessor() {
		return new CreatePersonalViewProcessor();
	}
	
	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public void setPvName(String pvName) {
		this.pvName = pvName;
	}

	public String getPvName() {
		return pvName;
	}

	public void setPvDes(String pvDes) {
		this.pvDes = pvDes;
	}

	public String getPvDes() {
		return pvDes;
	}

	private static class CreatePersonalViewProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof CreatePersonalViewRequest)) return false;
			return ((CreatePersonalViewRequest) req).getModelId() != null;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws ItemPersistenceException, StaleDataException,
				InvalidOperationException {
			
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid create_pv request");
			}
			
			CreatePersonalViewRequest r = (CreatePersonalViewRequest) req;
			Model m = DaoUtil.getModelDao().getById(r.getModelId(), false);
			if (m == null) {
				throw new InvalidOperationException("Invalid model ID: " + r.getModelId());
			}
			
			PersonalView pv = new PersonalView();
			DataItemUtil.setNewDataItemByUserId(pv, r.getRequesterId());
			pv.setName(r.getPvName());
			pv.setDescription(r.getPvDes());
			pv.setModel(m);
			m.addPersonalView(pv);
			
			pv = DaoUtil.getPersonalViewDao().save(pv);
			DaoUtil.getModelDao().save(m);
			
			CreatePersonalViewResponse rsp = new CreatePersonalViewResponse(r);
			rsp.setPvId(pv.getId());
			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			
			return true;
		}
		
	}
	
	public static class CreatePersonalViewResponse extends Response {
		private Long pvId;
		private String pvName;

		public CreatePersonalViewResponse(CreatePersonalViewRequest r) {
			super(r);
			this.setPvName(r.getPvName());
		}
		
		public void setPvId(Long pvId) {
			this.pvId = pvId;
		}

		public Long getPvId() {
			return pvId;
		}

		public void setPvName(String pvName) {
			this.pvName = pvName;
		}

		public String getPvName() {
			return pvName;
		}
		
	}
}
