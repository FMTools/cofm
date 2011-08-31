package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import util.StructureChangedException;

public class FmReader {

	static Logger logger = Logger.getLogger(FmReader.class);
	
	private class TreeNodeInfo {
		public int level;
		public int kind;
		public String name;
		
		public String toString() {
			return level + " " + kind + " " + name;
		}
	}
	
	private static final int NORMAL_FEATURE = 1;
	private static final int GROUP_INDICATOR = 2;
	private static final int GROUPED_FEATURE = 3;
	
	private int nextId = 0;
	private List<Integer> groupLevels = new ArrayList<Integer>();
	
	public FeatureModel readFromSplot(String splotXmlFile) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(splotXmlFile));
			FeatureModel fm = new FeatureModel();
			nextId = 0;
			groupLevels.clear();
			int maxLevel = 0;
			String s;
			boolean readingTree = false;
			int lastLevel = 0;
			Feature lastFeature = null;
			while ((s = in.readLine()) != null) {
				if (s.startsWith("<feature_model name=")) {
					fm.setName(extractModelName(s));
				} else if (s.startsWith("<feature_tree>")) {
					readingTree = true;
					continue;
				} else if (s.startsWith("</feature_tree>")) {
					readingTree = false;
				}
				
				if (!readingTree) {
					continue;
				}
				
				// Parse feature tree node
				TreeNodeInfo info = parseTreeNode(s);
				if (info.level > maxLevel) {
					maxLevel = info.level;
				}
				
				//logger.info(s + " -- " + info.toString());
				
				// We skip all group indicator
				if (info.kind == GROUP_INDICATOR) {
					continue;
				}
				
				Feature feature = new Feature(nextId++, info.name);
				
				if (info.level == 0) { 
					// Is a root
					fm.setRoot(feature);
				} else if (lastLevel < info.level) {
					// Is a child of last feature
					lastFeature.addChild(feature);
				} else if (lastLevel == info.level) {
					// Is a sibling of last feature
					lastFeature.getParent().addChild(feature);
				} else if (lastLevel > info.level) {
//					System.out.println("Cur = " + feature.getName() + " (" + info.level + "),"
//							+ " Last = " + lastFeature.getName() + " (" + lastLevel + ")");
					// Find current feature's parent
					while (lastLevel-- >= info.level) {
						lastFeature = lastFeature.getParent();
//						System.out.println("--> " + (lastFeature == null ? "null" : lastFeature.getName()));
					}
					
					lastFeature.addChild(feature);
				}
				lastLevel = info.level;
				lastFeature = feature;
			}
			
			fm.setNumFeatures(nextId);
			fm.setNumLevels(maxLevel + 1);
			return fm;
			
		} catch (FileNotFoundException e) {
			logger.error("Cannot open SPLOT xml file.", e);
			return null;
		} catch (IOException e) {
			logger.error("Read SPLOT file failed.", e);
			return null;
		} 
	}

	private String extractModelName(String s) {
		int begin = s.indexOf('"');
		int end = s.lastIndexOf('"');
		return s.substring(begin + 1, end);
	}

	private TreeNodeInfo parseTreeNode(String s) {
		TreeNodeInfo info = new TreeNodeInfo();
		
		
		// 1. level = "TAB" before ':'
		int begin = 0;
		for (; s.charAt(begin) != ':'; begin++) {
			// empty loop body
		}
		info.level = begin;
		
		// 2. kind, determined by the character after ':'
		begin++;
		if (s.charAt(begin) == 'g') {
			info.kind = GROUP_INDICATOR;
			// Record level of the group
			if (!groupLevels.contains(info.level)) {
				groupLevels.add(info.level);
			}
		} else {
			// Check finished group (whose level >= my_level)
			for (Iterator<Integer> iter = groupLevels.iterator(); iter.hasNext();) {
				if (iter.next().intValue() >= info.level) {
					iter.remove();
				}
			}
			// It belongs to group i where groupLevel[i] < my_level < groupLevel[i+1]
			int i = 0;
			while (i < groupLevels.size() && groupLevels.get(i) < info.level) {
				i++;
			}
			info.level = info.level - i;
			if (Character.isSpaceChar(s.charAt(begin))) {
				info.kind = GROUPED_FEATURE;
			} else {
				info.kind = NORMAL_FEATURE;
			}
		}
		
		// 3. name = text between first non-space and last '('
		begin++;
		while (Character.isSpaceChar(s.charAt(begin))) {
			begin++;   // skip all spaces
		}
		int end = s.lastIndexOf('(');
		if (end > begin) {
			info.name = s.substring(begin, end);
		} else {
			info.name = s.substring(begin);
		}
		
		return info;
	}

}
