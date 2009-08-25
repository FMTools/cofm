package collab.util.mock.client;

import collab.util.*;

public interface Engine {
	public Pair<String, ? extends HandlerOptions> nextRequest();
}
