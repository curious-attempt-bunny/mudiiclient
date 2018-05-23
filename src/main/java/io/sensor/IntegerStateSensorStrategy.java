package io.sensor;


public class IntegerStateSensorStrategy implements SensorStrategy {

	private final String stateKey;
	private final ObservableState observableState;

	public IntegerStateSensorStrategy(String stateKey, ObservableState observableState) {
		this.stateKey = stateKey;
		this.observableState = observableState;
	}

	public void onText(String text) {
		Integer valueOf = null;
		try {
			valueOf = Integer.valueOf(text);
		} catch (NumberFormatException e) {
			// suppress this
		}
		if (valueOf != null) {
			observableState.fireOnState(stateKey, valueOf);
		}
	}

}
