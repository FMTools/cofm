package collab.fm.server.bean.protocol;

import java.util.List;

import collab.fm.server.bean.transfer.BinaryRelation2;
import collab.fm.server.bean.transfer.Feature2;

public class UpdateResponse extends Response {

	private List<Feature2> features;
	private List<BinaryRelation2> binaries;
	
	public List<Feature2> getFeatures() {
		return features;
	}

	public void setFeatures(List<Feature2> features) {
		this.features = features;
	}

	public List<BinaryRelation2> getBinaries() {
		return binaries;
	}

	public void setBinaries(List<BinaryRelation2> binaries) {
		this.binaries = binaries;
	}
}
