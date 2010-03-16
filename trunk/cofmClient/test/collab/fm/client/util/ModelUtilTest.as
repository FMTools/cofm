package collab.fm.client.util {
	import flexunit.framework.TestCase;

	public class ModelUtilTest extends TestCase {
		public function testSortOnRating(): void {
			var input: Array = [
				{
					val: "music",
					v1: [1],
					v0: []
				},
				{
					val: "music player",
					v1: [1],
					v0: []
				}
				];
			ModelUtil.sortOnRating(
				input, "v1", "v0", 1, "val", "music");
			
			ModelUtil.sortOnRating(
				input, "v1", "v0", 1, "val", "xxx");
			
		}

		public function testSortOnArrayWithOneElement(): void {
			var input: Array = [
				{
					val: "music",
					v1: [1],
					v0: []
				}
				];
			ModelUtil.sortOnRating(
				input, "v1", "v0", 1, "val", "music");
			
		}

	}
}