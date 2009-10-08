package collab.fm.server.parser;

import java.util.List;

import collab.fm.server.bean.Operation;

public interface Parser {
	List<Operation> parse(Operation op, boolean needImplicitVotes);
	boolean isValid(Operation op);
}
