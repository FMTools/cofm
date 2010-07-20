package collab.fm.client.ui.validator {
	import mx.validators.ValidationResult;
	import mx.validators.Validator;

	public class EqualStringValidator extends Validator {

		private var _compareTo: String;
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
		
		public function get compareTo(): String {
			return _compareTo;
		}

		public function set compareTo(val: String): void {
			_compareTo = val;
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
				ok = (compareTo == String(value));
			} else {
				ok = (compareTo != String(value));
			}
			if (!ok) {
				_result.push(new ValidationResult(true, null, (checkEquals ? ERROR_NOT_EQUALS : ERROR_EQUALS), message));
			}
			return _result;
		}
	}
}