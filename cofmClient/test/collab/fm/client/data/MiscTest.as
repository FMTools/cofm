package collab.fm.client.data {
	import collab.fm.client.util.*;
	
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
		
		public function testXMLList(): void {
			trace ("list");
			var p: XMLList = new XMLList();
			p[0] = <abc/>;
			trace (" new list = '" + p.toXMLString() + "'");
		}
		
		public function testE4X(): void {
			var xml: XML = <node>
				<node id="1" bug="1"/>
				<node id="2" bug="0">
					<node id="3" bug="1"/>
				</node>
			</node>;
			var list: XMLListCollection = new XMLListCollection();
			list.source = new XMLList(xml);
			trace (XMLList(list.getItemAt(0)..node).toXMLString());
			trace("-----------");
			trace(XMLList(list.getItemAt(0)..node.(@id=="1")).toXMLString());	
			trace("-----------");
			trace(XMLList(list.getItemAt(0)..node.(@id=="3")).toXMLString());	
			trace("-----------");
			trace(XMLList(list.getItemAt(0)..node.(@bug=="1")).toXMLString());	
			
			var bugs: XMLList = list.getItemAt(0)..node.(@bug=="1");
			for (var i:int = bugs.length() - 1; i >= 0; i--) {
				delete bugs[i];
			}
			trace("-------------");
			trace(list.toXMLString());
		}
	}
}