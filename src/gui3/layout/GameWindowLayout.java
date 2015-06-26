package gui3.layout;

import gui3.ComponentWrapper;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class GameWindowLayout implements WindowLayout {
	
	private Map mapIdToComponent;
	
	public void doLayout() {
		// MainFrame
		JFrame frame = (JFrame) get(KEY_MAINFRAME);
		frame.getContentPane().removeAll();
		BorderLayout layout = new BorderLayout();
		frame.setLayout(layout);
	
		Component quickKey = get(KEY_QUICK_KEY);
		if (quickKey != null) {
			// north panel
			Container northPanel = (Container)get(KEY_NORTH_PANEL);
			northPanel.setLayout(new BorderLayout());
			northPanel.removeAll();
			frame.add(northPanel, BorderLayout.NORTH);
		
			// status bar
			northPanel.add(get(KEY_STATUS_BAR), BorderLayout.NORTH);
			
			// quick key
			northPanel.add(quickKey, BorderLayout.SOUTH);
		} else {
			// north (status bar)
			frame.add(get(KEY_STATUS_BAR), BorderLayout.NORTH);
		}
		
		// center panel
		frame.add(get(KEY_CENTER_PANEL), BorderLayout.CENTER);
		
		// scrollbar
		frame.remove(get(KEY_SCROLL_BAR)); // work-around for bizarre resistance to resizing when the status bar is made visible
		frame.add(get(KEY_SCROLL_BAR), BorderLayout.EAST);
		
		// south panel
		Container southPanel = (Container)get(KEY_SOUTH_PANEL);
		southPanel.setLayout(new BorderLayout());
		southPanel.removeAll();
		frame.add(southPanel, BorderLayout.SOUTH);
		
		// Prompt
		southPanel.add(get(KEY_PROMPT), BorderLayout.CENTER);
		
		// Config button
		southPanel.add(get(KEY_CONFIG), BorderLayout.EAST);
	
		get(KEY_PROMPT).requestFocus();
	
		adviseLayoutAwareComponents();
		
		((JComponent)frame.getContentPane()).revalidate();
		((JComponent)frame.getContentPane()).repaint();
	}

	private void adviseLayoutAwareComponents() {
		Iterator it = mapIdToComponent.values().iterator();
		while (it.hasNext()) {
			ComponentWrapper componentWrapper = (ComponentWrapper) it.next();
			
			if (componentWrapper instanceof LayoutAware) {
				((LayoutAware)componentWrapper).setLayout(this);
			}
		}
	}

	private Component get(String key) {
		ComponentWrapper componentWrapper = ((ComponentWrapper)mapIdToComponent.get(key));
		
		if (componentWrapper == null) {
			return null;
		}
		
		return componentWrapper.getComponent();
	}

	public void setMapIdToComponent(Map mapIdToComponent) {
		this.mapIdToComponent = mapIdToComponent;
	}

}
