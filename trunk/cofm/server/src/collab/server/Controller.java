package collab.server;

import collab.filter.Filter;
import collab.action.Action;

public interface Controller {
	public void addFilter(Filter f);
	public void addAction(Action a);
	public Object handleRequest(Object request);
}
