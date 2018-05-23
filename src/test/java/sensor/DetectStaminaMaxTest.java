package sensor;


public class DetectStaminaMaxTest extends DetectStateContext {
	protected void setUp() throws Exception {
		super.setUp();

		sensor.onCode("<890001>");
		sensor.onText("120");
	}
	
	public void testShouldChangeStateAfterClose() {
		sensor.onCode("<>");
		assertEquals(1, events.size());
		assertEquals("STAMINA_MAX = 120", events.get(0));
	}
}
