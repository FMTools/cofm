package cofm.component.validator
{
	import mx.validators.ValidationResult;
	import mx.validators.Validator;
	
	public class EqualStringValidator extends Validator {
		
		private var _compareWith: String;
		private var _equal: Boolean;
		private var _errMsg: String;
		
		private static const ERROR_NOT_EQUALS: String = "errorNotEquals";
		private static const ERROR_EQUALS: String = "errorEquals";
		
		public function get message(): String {
			return _errMsg;
		}
		
		public function set message(m: String): void {
			_errMsg = m;
		}
		
		public function get compareWith(): String {
			return _compareWith;
		}
		
		public function set compareWith(val: String): void {
			_compareWith = val;
		}
		
		public function get checkEquals(): Boolean {
			return _equal;
		}
		
		public function set checkEquals(b: Boolean): void {
			_equal = b;
		}
		
		public function EqualStringValidator() {
			super();
		}
		
		override protected function doValidation(value:Object): Array {
			var _result: Array = [];
			var ok: Boolean;
			if (checkEquals) {
				ok = (compareWith == String(value));
			} else {
				ok = (compareWith != String(value));
			}
			if (!ok) {
				_result.push(new ValidationResult(true, null, (checkEquals ? ERROR_NOT_EQUALS : ERROR_EQUALS), message));
			}
			return _result;
		}
	}
}