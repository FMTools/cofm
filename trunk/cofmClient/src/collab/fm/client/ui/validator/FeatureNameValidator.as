package collab.fm.client.ui.validator
{
	import collab.fm.client.data.FeatureNameList;
	import collab.fm.client.util.*;
	
	import mx.utils.StringUtil;
	import mx.validators.ValidationResult;
	import mx.validators.Validator;

	public class FeatureNameValidator extends Validator
	{
		private static const ERROR_DUPLICATE_NAME: String = "errDuplicateName";
		private static const ERROR_INVALID_NAME: String = "errInvalidName";
		private static const ERROR_EMPTY_NAME: String = "errEmptyName";
		
		private var _nameMustExist: Boolean;
		
		public function get nameMustExist(): Boolean {
			return _nameMustExist;
		}
		
		public function set nameMustExist(b: Boolean): void {
			_nameMustExist = b;
		}
		
		public function FeatureNameValidator()
		{
			super();
		}
		
		override protected function doValidation(value:Object):Array {
			var rslt: Array = [];
			var s: String = StringUtil.trim(String(value));
			if (s == "") {
				rslt.push(new ValidationResult(true, null, ERROR_EMPTY_NAME, RS.ERROR_NAME_INVALID));
				return rslt;
			}
			var nameExisted: Boolean = FeatureNameList.instance.contains(s);
			if (nameExisted && !nameMustExist) {
				rslt.push(new ValidationResult(true, null, ERROR_DUPLICATE_NAME, RS.ERROR_NAME_DUPLICATE));
			}
			if (!nameExisted && nameMustExist) {
				rslt.push(new ValidationResult(true, null, ERROR_INVALID_NAME, RS.ERROR_NAME_INVALID));
			}
			return rslt;
		}
		
	}
}