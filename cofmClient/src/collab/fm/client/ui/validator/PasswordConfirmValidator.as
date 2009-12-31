package collab.fm.client.ui.validator {
	import collab.fm.client.util.RS;

	import mx.validators.ValidationResult;
	import mx.validators.Validator;

	public class PasswordConfirmValidator extends Validator {

		private var _pwd: String;

		private const ERROR_PWD_CONFIRM: String = "errorPwdConfirmation";

		public function get pwd(): String {
			return _pwd;
		}

		public function set pwd(val: String): void {
			_pwd = val;
		}

		private var _result: Array;

		public function PasswordConfirmValidator() {
			super();
		}

		override protected function doValidation(value:Object): Array {
			_result = super.doValidation(value);
			if (_result.length > 0) {
				return _result;
			}
			if (pwd != String(value)) {
				_result.push(new ValidationResult(true, null, ERROR_PWD_CONFIRM, RS.REG_PWD_CONFIRM_ERROR));
			}
			return _result;
		}
	}
}