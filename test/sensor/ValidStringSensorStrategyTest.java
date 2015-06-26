package sensor;

import io.listener.StateListener;
import io.sensor.ObservableState;
import io.sensor.StringStateSensorStrategy;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class ValidStringSensorStrategyTest extends TestCase implements ObservableState {
	private static final String KEY = "aKey";
	private StringStateSensorStrategy strategy;
	private List events;

	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		events = new ArrayList();
		
		strategy = new StringStateSensorStrategy(KEY, this);
		strategy.onText("aString");
	}

	public void addStateListener(StateListener stateListener) {
		// N/A
	}

	public void fireOnState(String key, Object value) {
		assertTrue(value instanceof String);
		events.add(key+" = "+value);
	}
	
	public void testShouldChangeState() {
		assertEquals(1, events.size());
		assertEquals(KEY+" = aString", events.get(0));
	}
}
