package sensor;


public class DetectStaminaTest extends DetectStateContext {
	protected void setUp() throws Exception {
		super.setUp();

		sensor.onCode("<890000>");
		sensor.onText("96");
	}
	
	public void testShouldChangeStateAfterClose() {
		sensor.onCode("<>");
		assertEquals(1, events.size());
		assertEquals("STAMINA = 96", events.get(0));
	}
}
