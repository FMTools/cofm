package cofm.component.validator
{
	import cofm.model.FeatureNameList;
	import cofm.util.*;
	
	import mx.utils.StringUtil;
	import mx.validators.ValidationResult;
	import mx.validators.Validator;
	
	public class FeatureNameValidator extends Validator
	{
		public static const ERROR_DUPLICATE_NAME: String = "errDuplicateName";
		public static const ERROR_INVALID_NAME: String = "errInvalidName";
		public static const ERROR_EMPTY_NAME: String = "errEmptyName";
		
		private var _nameMustExist: Boolean;
		private var _allowEmpty: Boolean = false;
		
		public function get allowEmpty(): Boolean {
			return _allowEmpty;
		}
		
		public function set allowEmpty(b: Boolean): void {
			_allowEmpty = b;
		}
		
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
				if (allowEmpty) {
					return rslt;   // Validate OK
				}
				rslt.push(new ValidationResult(true, null, ERROR_EMPTY_NAME, RS.m_error_empty_name));
				return rslt;
			}
			var nameExisted: Boolean = FeatureNameList.instance().contains(s);
			if (nameExisted && !nameMustExist) {
				rslt.push(new ValidationResult(true, null, ERROR_DUPLICATE_NAME, RS.m_error_duplicate_name));
			}
			if (!nameExisted && nameMustExist) {
				rslt.push(new ValidationResult(true, null, ERROR_INVALID_NAME, RS.m_error_invalid_name));
			}
			return rslt;
		}
		
	}
}