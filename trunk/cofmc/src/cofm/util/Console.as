package cofm.util
{
	/**
	 * The API for outputing debug-like messages in the Feedback Window.
	 */
	public class Console
	{
		public static const MAX_MSG_LEN: int = 255;
		public static const MAX_MSG_COUNT: int = 50;
		
		public static var content: String = "";
		private static var count: int = 0;
		
		public static function info(msg: String): void {
			if (++count > MAX_MSG_COUNT) {
				Console.clear();
				count = 1;
			}
			content += "[" + new Date().toLocaleString() + "]  " + msg + "<br/>";
		}
		
		public static function warn(msg: String): void {
			if (++count > MAX_MSG_COUNT) {
				Console.clear();
				count = 1;
			}
			content += "<font color=\"#FF0000\">[" + new Date().toLocaleString() + "] WARNING:  " + msg + "</font><br/>";
		}
		
		public static function error(msg: String): void {
			if (++count > MAX_MSG_COUNT) {
				Console.clear();
				count = 1;
			}
			content += "<font color=\"#FF0000\"><b>[" + new Date().toLocaleString() + "] ERROR:  " + msg + "</b></font><br/>";
		}
		
		public static function clear(): void {
			content = "";
		}
		
		public static function trunc(msg: String): String {
			if (msg.length > MAX_MSG_LEN) {
				return msg.substring(0, MAX_MSG_LEN - 2) + "..";
			}
			return msg;
		}
	}
}