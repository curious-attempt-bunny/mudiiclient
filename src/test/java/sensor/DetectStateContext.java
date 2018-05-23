package sensor;

import io.listener.StateListener;
import io.sensor.StatsSensor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junit.framework.TestCase;

public abstract class DetectStateContext extends TestCase implements StateListener {

	protected List events;
	protected StatsSensor sensor;
	private Map mapKeyToValue;

	public DetectStateContext() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		events = new Vector();
		mapKeyToValue = new HashMap();
		
		sensor = new StatsSensor();
		sensor.addStateListener(this);
	}

	public void onState(String key, Object value) {
		events.add(key+" = "+value);
		mapKeyToValue.put(key, value);
	}

	public void testShouldNotChangeState() {
		assertEquals(0, events.size());
	}

	public abstract void testShouldChangeStateAfterClose();
	
	protected void expectKeyValue(String key, Object value) {
		assertEquals(value, mapKeyToValue.get(key));
	}
}