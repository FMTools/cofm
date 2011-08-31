package gen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;

public abstract class GenSplotXml {
	
	static Logger logger = Logger.getLogger(GenSplotXml.class);
	
	protected String genHeader(String fmName) {
		return "<feature_model name=\"" + fmName + "\">\n" +
			"<feature_tree>\n";
	}
	
	protected String genFooter() {
		return "</feature_tree>\n</feature_model>";
	}
	
	public void genXml(String fileName, String fmName) {
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(fileName));
			
			out.write(genHeader(fmName));
			
			genTree(out);
			
			out.write(genFooter());
			out.close();
		} catch (IOException e) {
			logger.error("Cannot open file: " + fileName, e);
		}
		
	}
	
	abstract protected void genTree(BufferedWriter out) throws IOException;

	protected String genNode(String name, int level) {
		StringBuilder sb = new StringBuilder();
		while (level-- > 0) {
			sb.append('\t');
		}
		sb.append(":m " + name + "\n");
		return sb.toString();
	}
}
