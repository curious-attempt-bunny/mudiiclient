package io.sensor;

import io.listener.CodeListener;
import io.listener.StateListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import domain.State;

public class StatsSensor implements Sensor, ObservableState {

	private static final int STATE_NONE = 0;

	private static final int STATE_POINTS = 1;

	private static final int STATE_SCORE = 5;

	private static final int STATE_FES = 6;

	private static final int STATE_MAGICALLY_BAD = 8;

	private static final int STATE_MAGICALLY_GOOD = 9;

	private static final int STATE_PHYSICALLY_BAD = 10;

	private static final int STATE_PHYSICALLY_GOOD = 11;
	
	private static final int STATE_DOCUMENT_READ = 12;

	private static final int STATE_GAME_MODE_TOGGLE = 13;

	private static final int STATE_ROOM_SHORT_NAME = 14;
	
	private int state = STATE_NONE;

	private int nesting = 0;

	private String text;

	private List stateListeners;

	private String lastCode;

	char[] seperators = new char[] {' ', '\t', '\r', '\n', '/'};

	private Vector codeListeners;
	     
	private Map mapCodeToSensorStrategy;

	private SensorStrategy sensorStrategy;

	private Map mapCodeFragementToSensorStrategy;
	
	public StatsSensor() {
		stateListeners = new Vector();
		codeListeners = new Vector();
		mapCodeToSensorStrategy = new HashMap();
		mapCodeFragementToSensorStrategy = new HashMap();
		
		mapCodeToSensorStrategy.put("<890000>", new IntegerStateSensorStrategy(State.KEY_STAMINA, this));
		mapCodeToSensorStrategy.put("<890001>", new IntegerStateSensorStrategy(State.KEY_STAMINA_MAX, this));
		mapCodeToSensorStrategy.put("<120201>", new QuickScoreStateSensorStrategy(this));
		mapCodeToSensorStrategy.put("<150000>", new StringStateSensorStrategy(State.KEY_DREAMWORD, this));
		
		mapCodeFragementToSensorStrategy.put("<97", new NullSensorStrategy()); // swallow snooped text
		
		sensorStrategy = null;
	}

	public void onCode(String code) {
		if (state == STATE_GAME_MODE_TOGGLE) {
			// HACK
			try {
				processStateText(state, text);
			} catch (Exception e) {
				e.printStackTrace();
			}
			resetState();
		}
		
		boolean isInitiallyInStateNone = isInStateNone();
		
		if (isInStateNone()) {
			sensorStrategy = (SensorStrategy) mapCodeToSensorStrategy.get(code);
			nesting = 1;
		}
		
		if (isInStateNone()) {
			Iterator it = mapCodeFragementToSensorStrategy.keySet().iterator();
			while (it.hasNext()) {
				String codeFragment = (String) it.next();
				
				if (code.startsWith(codeFragment)) {
					sensorStrategy = (SensorStrategy) mapCodeFragementToSensorStrategy.get(codeFragment);
					nesting = 1;
					
					mapCodeToSensorStrategy.put(code, sensorStrategy);
					
					break;
				}
			}
		}
		
		if (isInStateNone()) {
			if (code.equals("<1100>")) {
				state = STATE_MAGICALLY_BAD;
				nesting = 1;
			} else if (code.equals("<1101>")) {
				state = STATE_MAGICALLY_GOOD;
				nesting = 1;
			} else if (code.equals("<1120>")) {
				state = STATE_PHYSICALLY_BAD;
				nesting = 1;
			} else if (code.equals("<1121>")) {
				state = STATE_PHYSICALLY_GOOD;
				nesting = 1;
			} else if (code.equals("<8901>")) {
				state = STATE_POINTS;
				nesting = 1;
			} else if (code.equals("<1202>")) {
				state = STATE_SCORE;
				nesting = 1;
			} else if (code.equals("<120801>")) {
				state = STATE_FES;
				nesting = 1;
			} else if (code.equals("<00>")) {
				fireOnState(State.KEY_PLAYING, Boolean.FALSE);
				fireOnState(State.KEY_BLIND, Boolean.FALSE);
				fireOnState(State.KEY_CRIPPLED, Boolean.FALSE);
				fireOnState(State.KEY_DEAF, Boolean.FALSE);
				fireOnState(State.KEY_DUMB, Boolean.FALSE);
				fireOnState(State.KEY_ROOM_SHORT_NAME, null);
				
				state = STATE_GAME_MODE_TOGGLE;
			} else if (code.equals("<0201>")) {
				fireOnState(State.KEY_PLAYING, Boolean.TRUE);
				if (lastCode != null && lastCode.startsWith("<20")) {
					state = STATE_ROOM_SHORT_NAME;
					nesting = 1;
				}
			} else if (code.equals("<1206>")) {
				state = STATE_DOCUMENT_READ;
				nesting = 1;
			}
		}
		
		if (!isInitiallyInStateNone) {
			if (nesting > 0) {
				if (code.equals("<>")) {
					nesting--;
					if (nesting == 0) {
						try {
							processStateText(state, text);
						} catch (Exception e) {
							e.printStackTrace();
						}
						resetState();
					}
				} else {
					nesting++;
				}
			}
		}
		
		lastCode = code;
		
		fireOnCode(code);
	}

	private void resetState() {
		state = STATE_NONE;
		text = "";
		sensorStrategy = null;
	}

	private boolean isInStateNone() {
		return state == STATE_NONE && sensorStrategy == null;
	}

	private void fireOnCode(String code) {
		for (Iterator it = codeListeners.iterator(); it.hasNext();) {
			CodeListener codeListener = (CodeListener) it.next();
			
			codeListener.onCode(code);
		}
	}

	private void processStateText(int state, String text) {
		if (text == null) {
			text = "";
		}
		if (sensorStrategy != null) {
			sensorStrategy.onText(text);
		} else if (state == STATE_POINTS) {
			try {
				Integer points = Integer.valueOf(text.replaceAll(",", ""));
				fireOnState(State.KEY_POINTS, points);
			} catch (Exception e) {

			}
		} else if (state == STATE_SCORE) {
			try {
				Integer effstr = Integer
						.valueOf(getTokenAfter(text, "effective strength:\t"));
				fireOnState(State.KEY_STRENGTH_EFFECTIVE, effstr);
			} catch (Exception e) {

			}
			try {
				Integer effdex = Integer
						.valueOf(getTokenAfter(text, "effective dexterity:\t"));
				fireOnState(State.KEY_DEXTERITY_EFFECTIVE, effdex);
			} catch (Exception e) {
//				e.printStackTrace();
			}
			try {
				Integer effstr = Integer
						.valueOf(getTokenAfter(text, "strength:\t"));
				fireOnState(State.KEY_STRENGTH, effstr);
			} catch (Exception e) {

			}
			try {
				Integer effdex = Integer
						.valueOf(getTokenAfter(text, "dexterity:\t"));
				fireOnState(State.KEY_DEXTERITY, effdex);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Integer stamax = Integer.valueOf(getTokenAfter(text, "max:\t"));
				fireOnState(State.KEY_STAMINA_MAX, stamax);
			} catch (Exception e) {
				
			}
			try {
				Integer sta = Integer.valueOf(getTokenAfter(text, "stamina:\t"));
				fireOnState(State.KEY_STAMINA, sta);
			} catch (Exception e) {

			}
			try {
				Integer mag = Integer.valueOf(getTokenAfter(text, "magic:\t\t"));
				fireOnState(State.KEY_MAGIC, mag);
			} catch (Exception e) {
//				e.printStackTrace();
			}
			try {
				Integer points = Integer.valueOf(getTokenAfter(text, "score:\t")
						.replaceAll(",", ""));
				fireOnState(State.KEY_POINTS, points);
			} catch (Exception e) {

			}
		} else if (state == STATE_FES) {
			String[] elements = text.replace('\r', ' ').split(" ");
			
			try {
				Integer stamina_max = Integer.valueOf(elements[1]);
				fireOnState(State.KEY_STAMINA_MAX, stamina_max);
			} catch (Exception e) {
				
			}

			try {
				Integer stamina = Integer.valueOf(elements[0]);
				fireOnState(State.KEY_STAMINA, stamina);
			} catch (Exception e) {

			}

			try {
				Integer val = Integer.valueOf(elements[3]);
				fireOnState(State.KEY_STRENGTH, val);
			} catch (Exception e) {
				
			}

			try {
				Integer val = Integer.valueOf(elements[2]);
				fireOnState(State.KEY_STRENGTH_EFFECTIVE, val);
			} catch (Exception e) {

			}

			try {
				Integer val = Integer.valueOf(elements[5]);
				fireOnState(State.KEY_DEXTERITY, val);
			} catch (Exception e) {
				
			}

			try {
				Integer val = Integer.valueOf(elements[4]);
				fireOnState(State.KEY_DEXTERITY_EFFECTIVE, val);
			} catch (Exception e) {

			}
			
			try {
				Integer val = Integer.valueOf(elements[6]);
				fireOnState(State.KEY_MAGIC, val);
			} catch (Exception e) {

			}
			
			try {
				Integer points = Integer.valueOf(elements[8]
						.replaceAll(",", ""));
				fireOnState(State.KEY_POINTS, points);
			} catch (Exception e) {

			}	
			
			try {
				Integer resetMinutes = Integer.valueOf(elements[13]);
				fireOnState(State.KEY_RESET_TIME, resetMinutes);
			} catch (Exception e) {
				
			}
			
			Map mapWeatherCodeToDescription = new HashMap();
			mapWeatherCodeToDescription.put("F", "sunny");
			mapWeatherCodeToDescription.put("C", "cloudy");
			mapWeatherCodeToDescription.put("R", "raining");
			mapWeatherCodeToDescription.put("S", "snowing");
			mapWeatherCodeToDescription.put("O", "overcast");
			mapWeatherCodeToDescription.put("T", "thunder");
			mapWeatherCodeToDescription.put("B", "blizard");
			
			String desc = (String) mapWeatherCodeToDescription.get(elements[14]);
			if (desc == null) {
				desc = "???";
			}
			fireOnState(State.KEY_WEATHER, desc);
		} else if (state == STATE_MAGICALLY_BAD
				|| state == STATE_PHYSICALLY_BAD) {
			if (text.startsWith("You ")) {
				if (text.endsWith("deaf!")) {
					fireOnState(State.KEY_DEAF, Boolean.TRUE);
				} else if (text.endsWith("dumb!")) {
					fireOnState(State.KEY_DUMB, Boolean.TRUE);
				} else if (text.endsWith("crippled!")) {
					fireOnState(State.KEY_CRIPPLED, Boolean.TRUE);
				} else if (text.endsWith("blind!")) {
					fireOnState(State.KEY_BLIND, Boolean.TRUE);
				}
			}
		} else if (state == STATE_MAGICALLY_GOOD
				|| state == STATE_PHYSICALLY_GOOD) {
			if (text.startsWith("You ")) {
				if (text.endsWith("hearing!")) {
					fireOnState(State.KEY_DEAF, Boolean.FALSE);
				} else if (text.endsWith("voice!")) {
					fireOnState(State.KEY_DUMB, Boolean.FALSE);
				} else if (text.endsWith("ability to walk!")) {
					fireOnState(State.KEY_CRIPPLED, Boolean.FALSE);
				} else if (text.endsWith("sight!")) {
					fireOnState(State.KEY_BLIND, Boolean.FALSE);
				}
			}
		} else if (state == STATE_DOCUMENT_READ) {
			if (text.startsWith("The chart tells")) {
				int pos = text.indexOf("you should sail ");
				String[] items = text.substring(pos).split(" ");
				String dirs = "sw";
				int count = 1;
				for(int i=0; i<items.length; i++) {
					String dir = null;
					if (items[i].endsWith(",")) {
						items[i] = items[i].substring(0, items[i].length()-1);
					}
					if (items[i].equals("north")) {
						dir = "n";
					} else if (items[i].equals("northeast")) {
						dir = "ne";
					} else if (items[i].equals("northwest")) {
						dir = "nw";
					}
					if (dir != null) {
						dirs += ".";
						dirs += dir;
						count++;
					}
				}
				
				if (count != 6) {
					fireOnState(State.KEY_CHART_DIRS, "???");
				} else {
					fireOnState(State.KEY_CHART_DIRS, dirs);
				}
				
			}
		} else if (state == STATE_GAME_MODE_TOGGLE) {
			fireOnState(State.KEY_BLIND, Boolean.FALSE);
			fireOnState(State.KEY_DEAF, Boolean.FALSE);
			fireOnState(State.KEY_DUMB, Boolean.FALSE);
			fireOnState(State.KEY_CRIPPLED, Boolean.FALSE);
			
//			System.out.println("PLAY TEXT: "+text);
			if (text.indexOf("This reset is number ") != -1) {
				int pos = text.indexOf("This reset is number ") + "This reset is number ".length();
				int pos2 = text.indexOf(".", pos);
				int reset = Integer.parseInt(text.substring(pos, pos2).trim());
				fireOnState(State.KEY_RESET_NUMBER, new Integer(reset));
			}
		} else if (state == STATE_ROOM_SHORT_NAME) {
			fireOnState(State.KEY_ROOM_SHORT_NAME, text);
		}
	}

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

	public void onText(String text) {
		if (!isInStateNone()) {
			if (this.text == null) {
				this.text = text;
			} else {
				this.text = this.text + text;
			}
		}
	}

	/* (non-Javadoc)
	 * @see io.sensor.ObservableState#fireOnState(java.lang.String, java.lang.Object)
	 */
	public void fireOnState(String key, Object value) {
//		System.out.println(key + "=" + value);
		Iterator it = stateListeners.iterator();
		while(it.hasNext()) {
			StateListener stateListener = (StateListener) it.next();
			
			try {
				stateListener.onState(key, value);
			} catch (Exception e) {
				e.printStackTrace(); // TODO
			}
		}
	}

	/* (non-Javadoc)
	 * @see io.sensor.ObservableState#addStateListener(io.listener.StateListener)
	 */
	public void addStateListener(StateListener stateListener) {
		stateListeners.add(stateListener);
	}

	public void addCodeListener(CodeListener codeListener) {
		codeListeners.add(codeListener);
	}
}
