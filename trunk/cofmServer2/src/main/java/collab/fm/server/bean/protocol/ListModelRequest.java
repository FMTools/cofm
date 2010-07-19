package collab.fm.server.bean.protocol;

import java.util.ArrayList;
import java.util.List;

import collab.fm.server.bean.entity.Model;
import collab.fm.server.bean.entity.attr.Attribute;
import collab.fm.server.bean.entity.attr.Value;
import collab.fm.server.bean.transfer.Model2;
import collab.fm.server.processor.Processor;
import collab.fm.server.util.DaoUtil;
import collab.fm.server.util.Resources;
import collab.fm.server.util.exception.EntityPersistenceException;
import collab.fm.server.util.exception.InvalidOperationException;
import collab.fm.server.util.exception.StaleDataException;

public class ListModelRequest extends Request {
	private String searchWord;

	@Override
	protected Processor makeDefaultProcessor() {
		return new ListModelProcessor();
	}
	
	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}
	
	private static class ListModelProcessor implements Processor {

		public boolean checkRequest(Request req) {
			if (!(req instanceof ListModelRequest)) return false;
			ListModelRequest r = (ListModelRequest) req;
			if (r.getSearchWord() != null && r.getSearchWord().trim().isEmpty()) {
				r.setSearchWord(null);
			}
			return true;
		}

		public boolean process(Request req, ResponseGroup rg)
				throws EntityPersistenceException, StaleDataException,
				InvalidOperationException {
			if (!checkRequest(req)) {
				throw new InvalidOperationException("Invalid search_model operation.");
			}
			ListModelRequest r = (ListModelRequest)req;
			ListModelResponse rsp = new ListModelResponse(r);
			
			List<Model> all = null;
			if (r.getSearchWord() == null) {
				all = DaoUtil.getModelDao().getAll();
			} else {
				all = DaoUtil.getModelDao().getBySimilarName(r.getSearchWord());
			}
			
			// if no search word or search_word == "", then treat as exactly matches.
			if (r.getSearchWord() == null) {
				rsp.setExactlyMatches(true);
			}
			if (all != null) {
				for (Model m: all) {
					// Check exactly matches.
					if (rsp.isExactlyMatches() == false) {
						Attribute modelNames = m.getAttribute(Resources.ATTR_MODEL_NAME);
						if (modelNames != null) {
							for (Value v: modelNames.getValues()) {
								if (v.getStrVal().equals(r.getSearchWord())) {
									rsp.setExactlyMatches(true);
								}
							}
						}
					}
					
					// Add to result list.
					Model2 m2 = new Model2();
					m.transfer(m2);
					rsp.getModels().add(m2);
				}
			}

			rsp.setName(Resources.RSP_SUCCESS);
			rg.setBack(rsp);
			return true;
		}
		
	}
	
	public static class ListModelResponse extends Response {
		
		private List<Model2> models;
		private boolean exactlyMatches;
		
		public ListModelResponse(ListModelRequest r) {
			super(r);
			models = new ArrayList<Model2>();
			exactlyMatches = false;
		}
		
		public boolean isExactlyMatches() {
			return exactlyMatches;
		}

		public void setExactlyMatches(boolean exactlyMatches) {
			this.exactlyMatches = exactlyMatches;
		}

		public List<Model2> getModels() {
			return models;
		}

		public void setModels(List<Model2> models) {
			this.models = models;
		}
	}
	
}
