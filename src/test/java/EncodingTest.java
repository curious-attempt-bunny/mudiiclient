import junit.framework.TestCase;


public class EncodingTest extends TestCase {
	public void test() throws Exception {
		byte[] bytes = "£".getBytes("ISO-8859-15");
		assertEquals(1, bytes.length);
		assertEquals((byte)'£', bytes[0]);
	}
}
