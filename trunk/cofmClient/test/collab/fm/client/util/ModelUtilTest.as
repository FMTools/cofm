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
			var hits: Boolean = ModelUtil.sortOnRating(
				input, "v1", "v0", 1, "val", "music");
			assertTrue(hits);
			var noHits: Boolean = ModelUtil.sortOnRating(
				input, "v1", "v0", 1, "val", "xxx");
			assertFalse(noHits);
		}

		public function testSortOnArrayWithOneElement(): void {
			var input: Array = [
				{
					val: "music",
					v1: [1],
					v0: []
				}
				];
			var hits: Boolean = ModelUtil.sortOnRating(
				input, "v1", "v0", 1, "val", "music");
			assertTrue(hits);
		}

	}
}