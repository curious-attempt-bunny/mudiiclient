package gui3.layout;

import gui3.ComponentWrapper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.Map;

import javax.swing.JFrame;

public class LoginWindowLayout implements WindowLayout {
	private Map mapIdToComponent;

	public void doLayout() {
		// MainFrame
		JFrame frame = (JFrame) get(KEY_MAINFRAME);
		frame.getContentPane().removeAll();
		BorderLayout layout = new BorderLayout();
		frame.setLayout(layout);

		frame.add(get(KEY_LOGIN), BorderLayout.CENTER);
	}

	public void setMapIdToComponent(Map mapIdToComponent) {
		this.mapIdToComponent = mapIdToComponent;
	}

	private Component get(String key) {
		return ((ComponentWrapper) mapIdToComponent.get(key)).getComponent();
	}

}
