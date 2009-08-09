package collab.data;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


public class Response extends Filterable {
	public static final String TERMINATOR = "\0";
	
	public static final String TYPE_BACK = "back";
	public static final String TYPE_PEER = "peer";
	public static final String TYPE_BROADCAST = "broadcast";
	
	private String type;
	private List<String> targets = new ArrayList<String>(); 
	private Object body = new Body();
	
	public Response() {
		
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getTargets() {
		return targets;
	}

	public void addTarget(String target) {
		this.targets.add(target);
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
	
	public class Body {
		
		private String status;
		private Source source = new Source();
		private Object data;
		
		public Body() {
			
		}
		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public Source getSource() {
			return source;
		}

		public void setSource(Source source) {
			this.source = source;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}
		
		public class Source {
			private Integer id;
			private String name;
			private String user;
			private String address;
			public Source() {
				
			}
			public Integer getId() {
				return id;
			}
			public void setId(Integer id) {
				this.id = id;
			}
			public String getName() {
				return name;
			}
			public void setName(String name) {
				this.name = name;
			}
			public String getUser() {
				return user;
			}
			public void setUser(String user) {
				this.user = user;
			}
			public String getAddress() {
				return address;
			}
			public void setAddress(String address) {
				this.address = address;
			}
		}
	}
}
