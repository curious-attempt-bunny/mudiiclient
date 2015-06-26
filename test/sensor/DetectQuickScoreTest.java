package sensor;

import domain.State;

public class DetectQuickScoreTest extends DetectStateContext {

	protected void setUp() throws Exception {
		super.setUp();
		
		sensor.onCode("<120201>");
		sensor.onText("Bert\reff str 44      eff dex 52      sta ");
		sensor.onCode("<890000>");
		sensor.onCode("<9910>");
		sensor.onText("53");
		sensor.onCode("<>");
		sensor.onCode("<>");
		sensor.onText("/");
		sensor.onCode("<890001>");
		sensor.onCode("<9910>");
		sensor.onText("54");
		sensor.onCode("<>");
		sensor.onCode("<>");
		sensor.onText("   pts 4,210   gam 2");
	}
	
	public void testShouldChangeStateAfterClose() {
		sensor.onCode("<>");
		assertEquals(5, events.size());
		expectKeyValue(State.KEY_STRENGTH_EFFECTIVE, new Integer(44));
		expectKeyValue(State.KEY_DEXTERITY_EFFECTIVE, new Integer(52));
		expectKeyValue(State.KEY_STAMINA, new Integer(53));
		expectKeyValue(State.KEY_STAMINA_MAX, new Integer(54));
		expectKeyValue(State.KEY_POINTS, new Integer(4210));
	}

}
