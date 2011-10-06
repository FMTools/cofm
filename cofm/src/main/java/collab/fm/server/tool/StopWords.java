package collab.fm.server.tool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class StopWords {

	private static final String COMMENT = "|";
	private static final String FILE = "target/classes/eng-stop";
	
	public String toJavaCode(String file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String s;
		int i = 0;
		StringBuilder sb = new StringBuilder("{");
		while((s = in.readLine()) != null) {
			s = s.trim();
			if (s.length() < 1 || s.startsWith(COMMENT)) {
				continue;
			}
			
			String[] parts = s.split(" ");
			if (parts[0].length() > 0) {
				sb.append((i == 0 ? "" : ", ") + "\"" + parts[0] + "\"");
				i++;
				if (i % 10 == 0) {
					sb.append("\n");
				}
			}
		}
		sb.append("}");
		in.close();
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(new StopWords().toJavaCode(FILE));
	}
}
