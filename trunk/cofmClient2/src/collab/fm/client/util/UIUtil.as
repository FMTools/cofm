package collab.fm.client.util {
	import mx.core.UIComponent;

	public class UIUtil {
		public static function show(ui: UIComponent, isShow: Boolean): void {
			ui.includeInLayout = isShow;
			ui.visible = isShow;
		}

	}
}