package domain;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Configuration {
	private static final String FILENAME = "config.txt";

	public static final String KEY_COMMAND_REMAINS = "command.remains";
	public static final String KEY_ENTER_RESENDS = "enter.resends";
	public static final String KEY_LOGGING = "logging";
	public static final String KEY_ACTIVE_DATA_COLLECTION = "active.data.collection";
	public static final String KEY_MAX_WIDTH_80 = "max.width.80";
	public static final String KEY_INVERT_MOUSE_WHEEL_SCROLLING = "mouse.wheel.inverted";
	public static final String KEY_AUTO_PLAY = "auto.play";

	public static final int DEFAULT_ENTER_RESENDS = 1;
	public static final int DEFAULT_COMMAND_REMAINS = 0;
	public static final int DEFAULT_LOGGING = 1;
	public static final int DEFAULT_ACTIVE_DATA_COLLECTION = 1;
	public static final int DEFAULT_MAX_WIDTH_80 = 0;
	public static final int DEFAULT_INVERT_MOUSE_WHEEL_SCROLLING = 0;
	public static final int DEFAULT_AUTO_PLAY = 1;

	Properties properties;
	
	public String getSetting(String key, String defaultValue) {
		checkProperties();
		
		String value = (String) properties.get(key);
		if (value == null) {
			value = defaultValue;
		}
		
		return value;
	}

	private void checkProperties() {
		if (properties == null) {
			properties = new Properties() {
				public Set entrySet() {
					ArrayList sorted = new ArrayList(super.entrySet());
					Collections.sort(sorted, new Comparator() {
						public int compare(Object o1, Object o2) {
							String key1 = (String) ((Map.Entry) o1).getKey();
							String key2 = (String) ((Map.Entry) o2).getKey();
							return key1.compareTo(key2);
						}
					});
					return Collections.unmodifiableSet(new LinkedHashSet(sorted));
				}
			};
			try {
				properties.loadFromXML(new FileInputStream(FILENAME));
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
	}
	
	public String getSetting(String key) {
		return getSetting(key, null);
	}
	
	public void setSetting(String key, Object value) {
		checkProperties();
		
		if (value == null) {
			properties.setProperty(key, null);
		} else {
			properties.setProperty(key, value.toString());
		}
		
		try {
			properties.storeToXML(new FileOutputStream(FILENAME), "Do not edit this file! Especially not while the app is running. I warned you..");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int getInt(String key, int defaultValue) {
		int value = defaultValue;
		try {
			value = Integer.parseInt(getSetting(key));
		} catch (Exception e) {
			
		}
		return value;
	}

	public void setInt(String key, int value) {
		setSetting(key, Integer.toString(value));
	}

	public Properties getSettings() {
		checkProperties();

		return properties;
	}
}
