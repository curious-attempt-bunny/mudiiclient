package gui3;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JPanel;

public class Panel implements ComponentWrapper {

	private JPanel component;
//	private ComponentWrapper parent;

	public Component getComponent() {
		return component;
	}

	public void init() {
		component = new JPanel();
		
		component.setBackground(new Color(0,0,0));
		
//		component.setLayout(new BorderLayout());
		
//		Container container = (Container) parent.getComponent();
//		container.add(component, BorderLayout.SOUTH);
	}

//	public void setParent(ComponentWrapper parent) {
//		this.parent = parent;
//
//	}

}
