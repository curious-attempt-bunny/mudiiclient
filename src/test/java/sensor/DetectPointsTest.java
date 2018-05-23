package sensor;

import domain.State;


public class DetectPointsTest extends DetectStateContext {
	protected void setUp() throws Exception {
		super.setUp();

		// (-10 = <8901><9901>76,501<><>).
		sensor.onText("(-10 = ");
		sensor.onCode("<8901>");
		sensor.onCode("<9901>");
		sensor.onText("76,501");
	}
	
	public void testShouldChangeStateAfterClose() {
		sensor.onCode("<>");
		sensor.onCode("<>");
		sensor.onText(".");
		assertEquals(1, events.size());
		expectKeyValue(State.KEY_POINTS, new Integer(76501));
	}
}
