package cofm.component.fm
{
	import cofm.model.Model;
	import cofm.util.*;
	
	import mx.collections.XMLListCollection;
	
	public class AttributeSheetFactory
	{
		public static function createAttributeSheet(values: XMLList, attrDef: XML): AttributeSheet {
			var sheet: AttributeSheet = new AttributeSheet();
			sheet.attributeId = int(attrDef.@id);
			sheet.attributeName = attrDef.@name;
			sheet.attributeType = attrDef.@type;
			
			// I removed the "list-type" of attributes, so this field is always set to false.
			sheet.showValueIndex = false;
			
			// Show info for number-typed attributes (min, max, and units)
			if (sheet.attributeType == Cst.ATTR_TYPE_NUMBER) {
				var unit: String = attrDef.unit.text().toString();
				var min: Number = Number(attrDef.min.text().toString());
				var max: Number = Number(attrDef.max.text().toString());
				sheet.info = RS.m_fe_number_unit + ": " + unit + "    " +
							 RS.m_fe_number_min + ": " + min + "    " +
							 RS.m_fe_number_max + ": " + max;
			} else {
				sheet.info = "";
			}
			
			// Disallow new values for enumeration-typed attributes
			if (sheet.attributeType == Cst.ATTR_TYPE_ENUM) {
				sheet.allowNewValues = false;
			} else {
				sheet.allowNewValues = true;
			}
			
			// Set the values of the attribute. 
			setValuesForSheet(sheet, values, attrDef);
			
			// Set the size of the sheet
			sheet.width = Size.EDITOR_W;
			sheet.height = Size.EDITOR_H;
			
			return sheet;
		}
		
		public static function setValuesForSheet(sheet: AttributeSheet, values: XMLList, attrDef: XML): void {
			// The values of enumeration is NOT stored in "values" unless there's
			// any vote on it, so we need to add those non-voted values to the "values".
			var valcol: XMLListCollection = new XMLListCollection(values);
			if (sheet.attributeType == Cst.ATTR_TYPE_ENUM) {
				for each (var enum: Object in attrDef.enums.enum) {
					var enumStr: String = enum.text().toString();
					
					// If this enum-value is not in values
					if (XMLList(values.str.(text().toString()==enumStr)).length() <= 0) {
						// Append the enum-value (with 0 vote on it) to src
						valcol.addItem(
							<value> 
								<str>{enumStr}</str>
								<yes/><no/>
							</value>);
					}
				}
			}
			sheet.setValues(valcol.source);
		}
	}
}