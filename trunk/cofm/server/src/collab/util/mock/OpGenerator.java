package collab.util.mock;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import collab.data.Resources;
import collab.data.bean.Operation;
import collab.util.Pair;
import collab.util.Utils;

/**
 * @deprecated
 * @author Administrator
 *
 */
public class OpGenerator implements Generator<Object, String> {
	
	static Logger logger = Logger.getLogger(OpGenerator.class);
	
	private static OpGenerator g = null;
	private OpGenerator() {

	}
	
	private static class ReqField extends Pair<Class<?>, String[]> {
		public ReqField(Class<?> type, String[] possibleValues) {
			super(type, possibleValues);
		}
	}
	
	private static final String[] featureNames = {};
	private static ConcurrentHashMap<String, ReqField[]> rightOperandTable = 
		new ConcurrentHashMap<String, ReqField[]>();
	static {
		rightOperandTable.put(Resources.OP_ADDCHILD, new ReqField[] {
				new ReqField(String.class, featureNames),
				new ReqField(Integer.class, null)});
		rightOperandTable.put(Resources.OP_ADDDES, new ReqField[] {
				new ReqField(String.class, null)});
		rightOperandTable.put(Resources.OP_ADDEXCLUDE, new ReqField[]{
				new ReqField(String.class, featureNames),
				new ReqField(Integer.class, null)});
		rightOperandTable.put(Resources.OP_ADDNAME, new ReqField[] {
				new ReqField(String.class, featureNames)});
		rightOperandTable.put(Resources.OP_ADDREQUIRE, new ReqField[]{
				new ReqField(String.class, featureNames),
				new ReqField(Integer.class, null)});
		rightOperandTable.put(Resources.OP_SETEXT, new ReqField[] {
				new ReqField(String.class, null)});
		rightOperandTable.put(Resources.OP_SETOPT, new ReqField[] {
				new ReqField(String.class, null)});
	}
	
	private static final int maxFeatureNameLen = 8;
	private static final int maxFeatureId = 20;
	
	public static synchronized OpGenerator getInstance() {
		if (g == null) {
			g = new OpGenerator();
		}
		return g;
	}
	
	public Object next() {
		return next(Operation.NAMES[RandomUtils.nextInt(Operation.NAMES.length)]);
	}
	
	public Object next(String opName) {
		OpBean bean = new OpBean();
		bean.setOp(opName);
		bean.setLeft(Utils.randomIntOrString(maxFeatureId, maxFeatureNameLen, featureNames));
		bean.setRight(genRightOperand(opName));
		bean.setVote(new Boolean(RandomUtils.nextBoolean()));
		return bean;
	}
	
	private static Object genRightOperand(String op) {
		ReqField[] fields = rightOperandTable.get(op);
		if (fields == null) {
			return "Error-In-RightOperandTable";
		}
		int i = 0;
		if (fields.length > 0) {
			i = RandomUtils.nextInt(fields.length);
		}
		ReqField field = fields[i];
		if (field.first.equals(String.class)) {
			return Utils.randomString(maxFeatureNameLen, field.second);
		} else if (field.equals(Integer.class)) {
			return new Integer(RandomUtils.nextInt(maxFeatureId) + 1);
		}
		return "Error-In-RightOperandTable";
	}
}
