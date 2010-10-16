package collab.fm.server.bean.transfer;

import java.util.ArrayList;
import java.util.List;

public class EnumAttribute2 extends Attribute2 {
	protected List<String> enums = new ArrayList<String>();

	public List<String> getEnums() {
		return enums;
	}

	public void setEnums(List<String> enums) {
		this.enums = enums;
	}
	
	public void addEnum(String en) {
		this.enums.add(en);
	}
}
