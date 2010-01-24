package collab.fm.client.data {
	import flexunit.framework.TestCase;

	import mx.collections.XMLListCollection;

	public class MiscTest extends TestCase {
		public function testBoolInObject(): void {
			var bool: Boolean = false;
			var bool2: Boolean = true;
			var obj: Object = {"bool": bool, "bool2": bool2};
			assertFalse(obj.bool);
			assertTrue(obj.bool2);
		}

		public function testBoolInXML(): void {
			var bool: Boolean = false;
			var bool2: Boolean = true;
			var xml: XML = <me bool={bool} bool2={bool2}/>;
			assertTrue(xml.@bool=="false");
			assertTrue(xml.@bool2);
		}

		public function testE4X(): void {
			var xml: XML = <root>
					<user id="1" name="hoho"/>
					<user id="2" name="haha"/>
				</root>;
			var list: XMLListCollection = new XMLListCollection(new XMLList(xml.user));
			var s: XMLList = list.source.(@id==1);
			trace(s.toXMLString());
			assertTrue(s.length()>0);

		}
	}
}