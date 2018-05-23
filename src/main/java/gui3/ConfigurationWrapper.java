package gui3;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;

public class ConfigurationWrapper implements ComponentWrapper, ActionListener, MouseListener {
	
	private JButton component;
//	private ComponentWrapper parent;
	private WindowWrapper window;
	
	public Component getComponent() {
		return component;
	}

	public void init() {
		component = new JButton("config");
		
//		Container container = (Container) parent.getComponent();
//		container.add(component, BorderLayout.EAST);
		
		component.addActionListener(this);
		component.addMouseListener(this);
	}

//	public void setParent(ComponentWrapper parent) {
//		this.parent = parent;
//	}

	public void setWindow(WindowWrapper window) {
		this.window = window;
	}

	public void actionPerformed(ActionEvent arg0) {
		window.show();
		window.getComponent().requestFocus();
	}

	public void mouseClicked(MouseEvent arg0) {
		actionPerformed(null); // HACK
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

}
