package io.sensor;

import io.listener.StateListener;

public interface ObservableState {

	public abstract void fireOnState(String key, Object value);

	public abstract void addStateListener(StateListener stateListener);

}