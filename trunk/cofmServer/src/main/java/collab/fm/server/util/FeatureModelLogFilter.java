package collab.fm.server.util;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class FeatureModelLogFilter extends Filter {

	private String prefix;
	
	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Override
	public int decide(LoggingEvent arg0) {
		String msg = (String) arg0.getMessage();
		
		if (msg.startsWith(prefix)) {
			return ACCEPT;
		}
		return NEUTRAL;
	}

}
