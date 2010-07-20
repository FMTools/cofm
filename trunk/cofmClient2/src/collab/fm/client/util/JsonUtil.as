package collab.fm.client.util {

	import com.adobe.serialization.json.*;
	import mx.collections.ArrayCollection;

	public class JsonUtil {
		public static function jsonToArray(json: String): ArrayCollection {
			var a: Array = JSON.decode(json) as Array;
			return new ArrayCollection(a);
		}

		public static function jsonToObject(json: String): Object {
			return JSON.decode(json);
		}

		public static function objectToJson(obj: Object): String {
			return JSON.encode(obj);
		}
	}
}