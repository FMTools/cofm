package collab.fm.client.util
{
	import flexunit.framework.TestCase;
	
	import mx.collections.ArrayCollection;

	public class JsonUtilTest extends TestCase
	{
		public function testSimpleObject(): void {
			var json: String = '{"name": "lao yi", "age": 20}';
			//var ac: ArrayCollection = JsonUtil.fromJson(json);
			//assertEquals("lao yi", ac.getItemAt(0).name);
			var o: Object = JsonUtil.jsonToObject(json);
			assertEquals("lao yi", o.name);
			assertEquals(20, o.age);
		}
		
		public function testArrayWithSameTypeOfObjects(): void {
			var json: String = '[{"name": "yili", "hobby": "snooker"}' + 
					', {"name": "lao yi", "hobby": "billiard"}' + 
					', {"name": null, "hobby": "null"}]';
			var ac: ArrayCollection = JsonUtil.jsonToArray(json);
			assertEquals(3, ac.length);
			assertEquals("yili", ac.getItemAt(0).name);
			assertEquals("snooker", ac.getItemAt(0).hobby);
			assertNull(ac.getItemAt(2).name);
			assertEquals("null", ac.getItemAt(2).hobby);
		}
		
		public function testObjectWithSimpleArrayProperty(): void {
			var json: String = '{"name": "Mark", ' + 
					'"hobby": ["snooker", "billiard"]}';
			var o: Object = JsonUtil.jsonToObject(json);
			assertEquals("Mark", o.name);
			var a: Array = o.hobby as Array;
			assertEquals(2, a.length);
			assertEquals("snooker", a[0]);
			assertEquals("billiard", a[1]);
		}
		
		public function testObjectWithCompositeArrayProperty(): void {
			var json: String = '{"name": "Mark", ' + 
					'"hobby": [' + 
					'  {"name": "snooker", "ball": 21, "price": "high"},' + 
					'  {"name": "billiard", "ball": null, "price": "low"},' + 
					'  {"type": "sports", "favorite": true}' + 
					']}';
			var o: Object = JsonUtil.jsonToObject(json);
			assertEquals("Mark", o.name);
			var a: Array = o.hobby as Array;
			assertEquals(3, a.length);
			assertEquals(21, a[0].ball);
			assertNull(a[1].ball);
			assertEquals("sports", a[2].type);
			assertTrue(a[2].favorite);
		}
		
	}
}