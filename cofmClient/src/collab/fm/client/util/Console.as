package collab.fm.client.util
{
	
	public class Console
	{
		
		public static var content: String = "";
		
		public static function info(msg: String): void {
			content += "[" + new Date().toLocaleString() + "]  " + msg + "<br/>";
		}
		
		public static function warn(msg: String): void {
			content += "<font color=\"#FF0000\">[" + new Date().toLocaleString() + "] WARNING:  " + msg + "</font><br/>";
		}
		
		public static function error(msg: String): void {
			content += "<font color=\"#FF0000\"><b>[" + new Date().toLocaleString() + "] ERROR:  " + msg + "</b></font><br/>";
		}
		
		public static function clear(): void {
			content = "";
		}
	}
}