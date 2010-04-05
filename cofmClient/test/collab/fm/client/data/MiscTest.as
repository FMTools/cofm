package collab.fm.client.data {
	import collab.fm.client.util.*;

	import flexunit.framework.TestCase;

	import mx.collections.IViewCursor;
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
					<feature id="1" name="Play Control" controversy="1">
						<feature id="4" name="Advanced Control" controversy="1">
							<feature id="8" name="Slow" controversy="1"/>
							<feature id="9" name="Fast" controversy="1"/>
							<feature id="10" name="Repeat" controversy="1"/>
							<feature id="11" name="Volume" controversy="1"/>
							<feature id="100" />
							<feature id="12" name="Screen " controversy="1">
								<feature id="13" name="Resize" controversy="1"/>
								<feature id="14" name="Gamma Adjust" controversy="1"/>
								<feature id="100" />
							</feature>
						</feature>
					</feature>
					<feature id="2" name="Basic Control" controversy="1">
						<feature id="5" name="Stop" controversy="1"/>
						<feature id="6" name="Forward" controversy="1"/>
						<feature id="7" name="Back" controversy="1"/>
						<feature id="100" />
						<feature id="100" />
						<feature id="100" />
					</feature>
					<feature id="3" name="Play/Pause" controversy="1"/>
					<feature id="15" name="Sound" controversy="1"/>
				</root>;
			var list: XMLListCollection = new XMLListCollection(new XMLList(xml.feature));
			trace(list.source.toXMLString());
			trace("-------------------------");

			// Add a child.
			XML(list.source.(@id=="2")[0]).appendChild(<feature id="16" child="1"/>);
			trace(list.source.toXMLString());
			trace("---------------------------------");

			// Remove id == 5 (an inner node)
			delete list.source..feature.(@id=="5")[0];

			// Another way to remove inner nodes.
			while (XMLList(list.source..feature.(@id=="100")).length() > 0) {
				delete list.source..feature.(@id=="100")[0];
			}

			// Remove id == 3 (an outter node), which is different from inner ones.
			for (var cursor: IViewCursor = list.createCursor(); !cursor.afterLast; cursor.moveNext()) {
				if (cursor.current.@id=="3") {
					cursor.remove();
					break;
				}
			}

			// Another way to remove outter nodes.
			var index: int = list.getItemIndex(list.source.(@id=="15")[0]);
			list.removeItemAt(index);

			// The third way to remove outter nodes.


			trace(list.source.toXMLString());

			// Another test for ModelUtil.updateVote;
			var root2: XML = <feature>
					<yes><user>100</user></yes>
					<no><user>200</user></no>
				</feature>;
			delete root2.no.user.(text().toString() == "200")[0];
			trace(root2.toXMLString());
		}
	}
}