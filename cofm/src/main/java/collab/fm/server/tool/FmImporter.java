package collab.fm.server.tool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import collab.fm.server.bean.persist.Model;
import collab.fm.server.bean.persist.User;
import collab.fm.server.bean.persist.entity.Entity;

/**
 * Import FM from file, see resources/fm.import file for details
 * @author Yi Li
 *
 */
@Deprecated
public class FmImporter {

	static Logger logger = Logger.getLogger(FmImporter.class);
	
	private static final String COMMENT = "#";
	private static final String SKIP = "SKIP";
	private static final String MODEL = "MODEL";
	private static final String FEATURES = "FEATURES";
	private static final String FEATURE = "FEATURE";
	private static final String REFINEMENTS = "REFINEMENTS";
	private static final String CONSTRAINTS = "CONSTRAINTS";
	
	private static final String[][] acceptSymbol = {
		new String[] { MODEL },     // for "MODEL"
		new String[] { REFINEMENTS, CONSTRAINTS, MODEL },  // for "FEATURES"
		new String[] { FEATURE, REFINEMENTS, CONSTRAINTS, MODEL },   // for "FEATURE"
		new String[] { CONSTRAINTS, MODEL },    // for "REFINEMENTS"
		new String[] { MODEL }      // for "CONSTRAINTS"
	};
	
	private static final int N_MODEL = 0, N_FEATURES = 1, N_FEATURE = 2, N_REFINEMENTS = 3,
							 N_CONSTRAINTS = 4;
	
	private User user;
	private Model model;
	private Entity feature;
	
	private void parseFile(String file) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader(file));
		String s;
		int stage = N_MODEL;
		boolean skip = false;
		while ((s = in.readLine()) != null) {
			if (s.startsWith(COMMENT)) {
				continue;
			}
			
			if (s.startsWith(SKIP)) {
				skip = true;
				continue;
			}
			
			if (skip && findNextAcceptSymbol(s, stage)) {
				
			}
		}
	}

	private boolean findNextAcceptSymbol(String s, int stage) {
		// TODO Auto-generated method stub
		return false;
	}
}
