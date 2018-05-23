package io.sensor;

import domain.State;

public class QuickScoreStateSensorStrategy implements SensorStrategy {

	private final ObservableState observableState;

	public QuickScoreStateSensorStrategy(ObservableState observableState) {
		this.observableState = observableState;
		// TODO Auto-generated constructor stub
	}

	public void onText(String text) {
		try {
			Integer effstr = Integer
					.valueOf(getTokenAfter(text, "eff str "));
			observableState.fireOnState(State.KEY_STRENGTH_EFFECTIVE, effstr);
		} catch (Exception e) {

		}
		try {
			Integer effdex = Integer
					.valueOf(getTokenAfter(text, "eff dex "));
			observableState.fireOnState(State.KEY_DEXTERITY_EFFECTIVE, effdex);
		} catch (Exception e) {
//			e.printStackTrace();
		}
		try {
			Integer stamax = Integer.valueOf(getTokenAfter(text, "/"));
			observableState.fireOnState(State.KEY_STAMINA_MAX, stamax);
		} catch (Exception e) {
			
		}
		try {
			Integer sta = Integer.valueOf(getTokenAfter(text, "sta "));
			observableState.fireOnState(State.KEY_STAMINA, sta);
		} catch (Exception e) {

		}
		try {
			Integer mag = Integer.valueOf(getTokenAfter(text, "mag "));
			observableState.fireOnState(State.KEY_MAGIC, mag);
		} catch (Exception e) {

		}
		try {
			Integer points = Integer.valueOf(getTokenAfter(text, "pts ")
					.replaceAll(",", ""));
			observableState.fireOnState(State.KEY_POINTS, points);
		} catch (Exception e) {

		}
	}

	char[] seperators = new char[] {' ', '\t', '\r', '\n', '/'};

	private String getTokenAfter(String string, String pattern) {
		String token = null;
		int pos = string.indexOf(pattern);
		if (pos != -1) {
			pos += pattern.length();
			int pos2 = -1;
			for(int i=0; i<seperators.length; i++) {
				int pos3 = string.indexOf(seperators[i], pos);
				if (pos2 == -1 || (pos3 != -1 && pos3 < pos2)) {
					pos2 = pos3;
				}
			}
			if (pos2 == -1) {
				pos2 = string.length();
			}
			token = string.substring(pos, pos2).trim();
		}
		return token;
	}
}
