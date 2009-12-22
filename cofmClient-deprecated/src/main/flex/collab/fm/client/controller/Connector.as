package collab.fm.client.controller
{
	import flash.net.XMLSocket;
	
	public class Connector
	{
		// Listening the reponse from server and build approriate command to handle it
		
		private static var instance: Connector = new Connector();
		
		private var host: String;
		private var port: int;
		private var socket: XMLSocket;
		
		public static function getInstance(): Connector {
			return instance;
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