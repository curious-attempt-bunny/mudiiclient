package io.sensor;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class PlayerSensor implements Sensor {
	private String text;
//	private int state;
	private int nesting;
	private List playerListeners;
	
	public PlayerSensor() {
		playerListeners = new Vector();
	}
	
	public void onCode(String code) {
		if (code.startsWith("<05")) {
			nesting = 1;
			text = null;
		} else if (code.equals("<>")) {
			if (nesting > 0) {
				nesting--;
				
				if (nesting == 0 && text != null) {
					process(text);
					text = null;
				}
			}
		}
	}

	private void process(String text) {
		String name = text;
		if (name.indexOf("(") != -1 && name.indexOf(")") != -1) {
			name = name.substring(name.lastIndexOf("(")+1,name.indexOf(")"));
		}
		if (name.indexOf(" the ") != -1) {
			name = name.substring(0,name.indexOf(" the "));
		}
		if (name.indexOf(" is ") != -1) {
			name = name.substring(0,name.indexOf(" is "));
		}
		if (name.indexOf(" has ") != -1) {
			name = name.substring(0,name.indexOf(" has "));
		}
		if (name.indexOf(" fades ") != -1) {
			name = name.substring(0,name.indexOf(" fades "));
		}
		if (name.startsWith("Sir ") || name.startsWith("Lady ") || name.startsWith("Brother ") || name.startsWith("Sister ") ) {
			name = name.substring(name.indexOf(" ")+1);
		}
		if (name.indexOf(" ") == -1) {
			fireOnPlayer(name);
		}
	}

	private void fireOnPlayer(String name) {
		for (Iterator it = playerListeners.iterator(); it.hasNext();) {
			PlayerListener playerListener = (PlayerListener) it.next();
			
			playerListener.onPlayer(name);
		}
	}

	public void onText(String text) {
		if (nesting > 0) {
			if (this.text == null) {
				this.text = text;
			} else {
				this.text += text;
			}
		}
	}

	public void addPlayerSensor(PlayerListener playerListener) {
		playerListeners.add(playerListener);
	}

}
