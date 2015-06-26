package sensor;

import io.listener.StateListener;
import io.sensor.IntegerStateSensorStrategy;
import io.sensor.ObservableState;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class ValidIntegerSensorStrategyTest extends TestCase implements ObservableState {
	private static final String KEY = "aKey";
	private IntegerStateSensorStrategy strategy;
	private List events;

	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		events = new ArrayList();
		
		strategy = new IntegerStateSensorStrategy(KEY, this);
		strategy.onText("12");
	}

	public void addStateListener(StateListener stateListener) {
		// N/A
	}

	public void fireOnState(String key, Object value) {
		assertTrue(value instanceof Integer);
		events.add(key+" = "+value);
	}
	
	public void testShouldChangeState() {
		assertEquals(1, events.size());
		assertEquals(KEY+" = 12", events.get(0));
	}
}
