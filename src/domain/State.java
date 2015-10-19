package domain;

import io.listener.StateListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class State implements StateListener {
	public static final String KEY_DREAMWORD = "DREAMWORD";
	
	public static final String KEY_MAGIC = "MAGIC";

	public static final String KEY_DEXTERITY_EFFECTIVE = "DEXTERITY_EFFECTIVE";

	public static final String KEY_STRENGTH_EFFECTIVE = "STRENGTH_EFFECTIVE";

	public static final String KEY_STAMINA_MAX = "STAMINA_MAX";

	public static final String KEY_STAMINA = "STAMINA";

	public static final String KEY_POINTS = "POINTS";

	public static final String KEY_STRENGTH = "STRENGTH";

	public static final String KEY_DEXTERITY = "DEXTERITY";

	public static final String KEY_DEAF = "DEAF";

	public static final String KEY_DUMB = "DUMB";

	public static final String KEY_CRIPPLED = "CRIPPLED";

	public static final String KEY_BLIND = "BLIND";

	public static final String KEY_BONUS_STRENGTH = "BONUS_STRENGTH";

	public static final String KEY_BONUS_DEXTERITY = "BONUS_DEXTERITY";

	public static final String KEY_BONUS_STAMINA = "BONUS_STAMINA";

	public static final String KEY_WEATHER = "WEATHER";

	public static final String KEY_PLAYING = "PLAYING";

	public static final String KEY_CHART_DIRS = "CHART_DIRS";

	public static final String KEY_RESET_NUMBER = "RESET_NUMBER";

	public static final String KEY_RESET_TIME = "RESET_TIME";

	public static final String KEY_ROOM_SHORT_NAME = "ROOM_SHORT_NAME";
	
	private Map mapStateToValue;
	private List stateListeners;

	public State() {
		mapStateToValue = new HashMap();
		stateListeners = new Vector();
	}
	
	public Object get(String key) {
		return mapStateToValue.get(key);
	}

	public void onState(String key, Object value) {
		if (key == KEY_DEXTERITY_EFFECTIVE) {
			checkMaximum(KEY_DEXTERITY, value);
		} else if (key == KEY_STRENGTH_EFFECTIVE) {
			checkMaximum(KEY_STRENGTH, value);
		} else if (key == KEY_STAMINA) {
			checkMaximum(KEY_STAMINA_MAX, value);
		} else if (key == KEY_MAGIC) {
			checkMaximum(KEY_STAMINA_MAX, value);
		}
		Object oldVal = mapStateToValue.put(key, value);
		if (value == null || !value.equals(oldVal)) { 
			// avoid refiring same values
			fireOnState(key, value);
			
			if (key == KEY_RESET_NUMBER) {
				onState(KEY_CHART_DIRS, null); // blank chart dirs apon new reset
			}
		}
	}

	private void checkMaximum(String maxKey, Object value) {
		Object max = mapStateToValue.get(maxKey);
		if (max == null || ((Integer)max).intValue() < ((Integer)value).intValue())
		{
			onState(maxKey, value);
		}
	}
	
	private void fireOnState(String key, Object value) {
		System.out.println(key + "=" + value);
		Iterator it = stateListeners.iterator();
		while(it.hasNext()) {
			StateListener stateListener = (StateListener) it.next();
			
			stateListener.onState(key, value);
		}
	}

	public void addStateListener(StateListener stateListener) {
		stateListeners.add(stateListener);
	}
}
