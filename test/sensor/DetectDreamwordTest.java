package sensor;

import domain.State;


public class DetectDreamwordTest extends DetectStateContext {
	protected void setUp() throws Exception {
		super.setUp();

		sensor.onCode("<150000>");
		sensor.onText("cleanCode");
	}
	
	public void testShouldChangeStateAfterClose() {
		sensor.onCode("<>");
		assertEquals(1, events.size());
		expectKeyValue(State.KEY_DREAMWORD, "cleanCode");
	}
}
