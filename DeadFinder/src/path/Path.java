package path;

import java.util.ArrayList;
import java.util.List;

import model.Feature;

public class Path {

	private List<Feature> nodes = new ArrayList<Feature>();

	public int length() {
		return nodes.size();
	}
	
	public void updateByDead(Feature dead) {
		boolean deadAppeared = false;
		for (int i = 0; i < nodes.size(); i++) {
			Feature f = nodes.get(i);
			if (deadAppeared) {
				f.setDead(Feature.DEAD);
			} else {
				if (dead != null && f.equals(dead)) {
					f.setDead(Feature.DEAD);
					deadAppeared = true;
				} else {
					f.setDead(Feature.ALIVE);
				}
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nodes.size() - 1; i++) {
			sb.append(nodes.get(i).toString() + ", ");
		}
		sb.append(nodes.get(nodes.size() - 1).toString());
		return sb.toString();
	}
	
	public void addNode(Feature f) {
		nodes.add(f);
	}
	
	public void prependNode(Feature f) {
		nodes.add(0, f);
	}
	
	public void setNodes(List<Feature> nodes) {
		this.nodes = nodes;
	}

	public List<Feature> getNodes() {
		return nodes;
	}

}
