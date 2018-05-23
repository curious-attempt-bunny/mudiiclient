package io.sensor;



public class StringStateSensorStrategy implements SensorStrategy {

	private final ObservableState observableState;
	private final String key;

	public StringStateSensorStrategy(String key, ObservableState observableState) {
		this.key = key;
		this.observableState = observableState;
	}

	public void onText(String text) {
		observableState.fireOnState(key, text);
	}

}
