package collab.fm.client.cmn {
	import collab.fm.client.util.*;

	import flash.events.DataEvent;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.SecurityErrorEvent;
	import flash.net.XMLSocket;

	import mx.controls.Alert;

	public class Connector {
		// Send and receive data from/to server 

		private static var con: Connector = new Connector();

		private var host: String;
		private var port: int;
		private var socket: XMLSocket;

		public static function get instance(): Connector {
			return con;
		}

		public function Connector() {
			socket = new XMLSocket();
			socket.addEventListener(DataEvent.DATA, onData);
			socket.addEventListener(IOErrorEvent.IO_ERROR, onIoError);
			socket.addEventListener(SecurityErrorEvent.SECURITY_ERROR, onSecurityError);
		}

		public function setAddress(host: String, port: int): void {
			this.host = host;
			this.port = port;
		}

		public function connect(handler: Function): void {
			//reconnect a new address
			if (socket != null && socket.connected) {
				socket.close();
			}
			socket.addEventListener(Event.CONNECT, handler);
			socket.connect(this.host, this.port);
		}

		// Commands call send() to send data, and if necessary, write themselves into command buffer
		public function send(data: String): void {
			socket.send(data);
			trace("--- Data sent as: " + data);
		}

		private function onData(evt: DataEvent): void {
			trace("--- Data received as: " + evt.data);
			var sdata: Object = JsonUtil.jsonToObject(evt.data);
			ServerDataDispatcher.dispatchData(sdata);
		}

		private function onIoError(evt: IOErrorEvent): void {
			Alert.show("Maintaining server... Please try later.", "Couldn't connect server");
		}

		private function onSecurityError(evt: SecurityErrorEvent): void {
			Alert.show("Unfortunately, a security error has occured: " + evt.text, "Security Error");
		}
	}
}