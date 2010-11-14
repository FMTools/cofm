package cofm.component.fm
{
	import cofm.model.Model;
	import cofm.util.*;
	
	public class AttributeSheetFactory
	{
		public static function createAttributeSheet(src: XML): AttributeSheet {
			var sheet: AttributeSheet = new AttributeSheet();
			sheet.attributeName = src.@name;
			sheet.attributeType = src.@type;
			
			// Disallow new values for enumeration-typed attributes
			if (sheet.attributeType == Cst.ATTR_TYPE_ENUM) {
				sheet.allowNewValues = false;
			} else {
				sheet.allowNewValues = true;
			}
			
			// Show value index for list-typed attributes
			if (sheet.attributeType == Cst.ATTR_TYPE_LIST) {
				sheet.showValueIndex = true;
			} else {
				sheet.showValueIndex = false;
			}
			
			// Show info for number-typed attributes (min, max, and units)
			if (sheet.attributeType == Cst.ATTR_TYPE_NUMBER) {
				var unit: String = XML(src.unit).text().toString();
				var min: Number = Number(XML(src.min).text().toString());
				var max: Number = Number(XML(src.max).text().toString());
				sheet.info = RS.m_fe_number_unit + ": " + unit + "    " +
							 RS.m_fe_number_min + ": " + min + "    " +
							 RS.m_fe_number_max + ": " + max;
			} else {
				sheet.info = "";
			}
			
			// Set the values of the attribute. 
			setValuesForSheet(sheet, src);
			
			// Set the size of the sheet
			sheet.width = Size.EDITOR_W;
			sheet.height = Size.EDITOR_H;
			
			return sheet;
		}
		
		public static function setValuesForSheet(sheet: AttributeSheet, src: XML): void {
			// The values of enumeration is NOT stored in "src" unless there's
			// any vote on it, so we need to add those non-voted values to the "src". 
			// (See "setValuesForEnum()" for details.)
			if (sheet.attributeType == Cst.ATTR_TYPE_ENUM) {
				var allEnums: XMLList = XMLList(Model.instance().attrs
										.source.(@name==String(src.@name))[0]
										.enums.enum);  // get all possible enum-values
				for each (var enum: Object in allEnums) {
					var enumStr: String = XML(enum).text().toString();
					
					// If this enum-value is not in src.values
					if (XMLList(src..str.(text().toString()==enumStr)).length() <= 0) {
						// Append the enum-value (with 0 vote on it) to src
						if (src.values == undefined) {
							XML(src).appendChild(<values/>);
						}
						XML(src.values).appendChild(
							<value> 
								<str>{enumStr}</str>
								<yes/><no/>
							</value>);
					}
				}
			}
			sheet.setValues(src);
		}
	}
}