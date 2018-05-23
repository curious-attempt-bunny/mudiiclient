package gui3.layout;

import java.util.Map;

public interface WindowLayout {
	String KEY_MAINFRAME = "MAINFRAME";
	String KEY_STATUS_BAR = "STATUS_BAR";
	String KEY_SCROLL_BAR = "SCROLL_BAR";
	String KEY_SOUTH_PANEL = "SOUTH_PANEL";
	String KEY_PROMPT = "PROMPT";
	String KEY_CONFIG = "CONFIG";
	String KEY_CENTER_PANEL = "CENTER_PANEL";
	String KEY_LOGIN = "LOGIN";
	String KEY_NORTH_PANEL = "NORTH_PANEL";
	String KEY_QUICK_KEY = "QUICK_KEY";

	void doLayout();
	void setMapIdToComponent(Map mapIdToComponent);
}
