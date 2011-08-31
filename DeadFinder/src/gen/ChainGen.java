package gen;

import java.io.BufferedWriter;
import java.io.IOException;

public class ChainGen extends GenSplotXml {

	private int level;

	public static void main(String[] args) {
		GenSplotXml gen = new ChainGen(100);
		gen.genXml("chain_100.xml", "Chain_100");
	}
	
	public ChainGen(int level) {
		this.setLevel(level);
	}
	
	@Override
	protected void genTree(BufferedWriter out) throws IOException {
		for (int i = 0; i < level; i++) {
			out.write(genNode("F_" + i, i));
		}
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}
