package collab.fm.client.event {
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;

	public class ClientEvtDispatcher extends EventDispatcher {
		private static var _me: ClientEvtDispatcher = new ClientEvtDispatcher();

		public static function instance(): ClientEvtDispatcher {
			return _me;
		}

		public function ClientEvtDispatcher(target:IEventDispatcher=null) {
			super(target);
		}

	}
}