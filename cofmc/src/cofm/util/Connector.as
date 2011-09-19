package cofm.util
{
	import cofm.event.ClientEvent;
	
	import flash.events.DataEvent;
	import flash.events.Event;
	import flash.events.IOErrorEvent;
	import flash.events.SecurityErrorEvent;
	import flash.events.TimerEvent;
	import flash.external.ExternalInterface;
	import flash.net.XMLSocket;
	import flash.utils.Timer;
	
	import mx.controls.Alert;
	
	/**
	 * Send data to and receive from the server.
	 */
	public class Connector {
		
		private static var con: Connector = new Connector();
		
		private var clientId: int;
		private var hbTimer: Timer;  // Heart-beat timer
		
		private const HEART_BEAT_INTERVAL: Number = 1000 * 30; // 30 seconds
		
		public static function instance(): Connector {
			return con;
		}
		
		public function Connector() {
			
		}
		
		public function init(): void {
			if (ExternalInterface.available) {
				ExternalInterface.addCallback("handleResponse", handleResponse);
				ExternalInterface.addCallback("notifyConnected", notifyConnected);
				ExternalInterface.call("postData", "", "handshake");  // send a handshake message when init
			}
			hbTimer = new Timer(HEART_BEAT_INTERVAL);
			hbTimer.addEventListener(TimerEvent.TIMER, onHeartBeat);
		}
		
		private function onHeartBeat(evt: TimerEvent): void {
			if (ExternalInterface.available) {
				ExternalInterface.call("postData", "", "heartbeat");
			}
		}
		
		public function disconnect(): void {
			if (ExternalInterface.available) {
				ExternalInterface.call("postData", "", "quit");
			}
		}
		
		public function send(data: Object): void {
			if (ExternalInterface.available) {
				var json: String = JsonUtil.objectToJson(encodeQuotes(data));
				ExternalInterface.call("postData", json);
				trace("<<<--- Data sent: " + json + "\n");
			}
		}
		
		public function notifyConnected(): void {
			ClientEvtDispatcher.instance().dispatchEvent(
				new ClientEvent(ClientEvent.CONNECT_SUCCESS));
			hbTimer.start();
		}
		
		public function handleResponse(res: Object): void {
			trace("--->>> Data received: " + String(res));
			if (res == null || String(res).length <= 0) {
				return;
			}
			var sdata: Object = decodeQuotes(JsonUtil.jsonToObject(String(res)));
			trace ("Received " + (sdata as Array).length + " response(s).");
			ServerDataDispatcher.dispatchData(sdata);
		}
		
		private static const SINGLE_QUOTE: String = "_squote_";
		private static const DOUBLE_QUOTE: String = "_dquote_";
		
		private function encodeQuotes(data: Object): Object {
			var result: Object;
			if (data is Array) {
				result = new Array();
			} else {
				result = new Object();
			}
			for (var prop: Object in data) {
				if (typeof(data[prop]) == "string") {
					result[prop] = String(data[prop]).replace(/"/g, DOUBLE_QUOTE)
						.replace(/'/g, SINGLE_QUOTE);
				} else if (typeof(data[prop]) == "object") {
					result[prop] = encodeQuotes(data[prop]);
				} else {
					result[prop] = data[prop];
				}
			}
			return result;
		}
		
		private function decodeQuotes(data: Object): Object {
			var result: Object;
			if (data is Array) {
				result = new Array();
			} else {
				result = new Object();
			}
			for (var prop: Object in data) {
				if (typeof(data[prop]) == "string") {
					result[prop] = String(data[prop]).replace(/_dquote_/g, "\"")
						.replace(/_squote_/g, "'");
				} else if (typeof(data[prop]) == "object") {
					result[prop] = decodeQuotes(data[prop]);
				} else {
					result[prop] = data[prop];
				}
			}
			return result;
		}
		
	}
}