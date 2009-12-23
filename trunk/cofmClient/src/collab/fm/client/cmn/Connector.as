package collab.fm.client.cmn
{
	import flash.net.XMLSocket;
	
	public class Connector
	{
		// Send and receive data from/to server 
		
		private static var con: Connector = new Connector();
		
		private var host: String;
		private var port: int;
		private var socket: XMLSocket;
		
		public static function get instance(): Connector {
			return con;
		}
		
		public function setAddress(host: String, port: int): void {
			this.host = host;
			this.port = port;
		}
		
		// Commands call send() to send data, and if necessary, write themselves into command buffer
		public function send(data: Object): void {
			
		}
		
		private function Connector()
		{
		}

	}
}