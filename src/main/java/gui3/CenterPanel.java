package gui3;

import gui3.text.TextAreaWrapper;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

public class CenterPanel implements ComponentWrapper {

	private JSplitPane component;
	private ComponentWrapper parent;
	private TextAreaWrapper mainText;
	private TextAreaWrapper scrollback;

	public Component getComponent() {
		return (component == null ? mainText.getComponent() : component);
	}

	public void init() {
		// controlled by ScrollbackController
	}

	public void enable() {
		JFrame container = (JFrame) parent.getComponent();
		
		container.getContentPane().remove(mainText.getComponent());
		
		component = new JSplitPane(JSplitPane.VERTICAL_SPLIT); //, scrollback.getComponent(), mainText.getComponent());
		component.setTopComponent(scrollback.getComponent());
		component.setBottomComponent(mainText.getComponent());
		component.setDividerSize(3);
		component.setResizeWeight(1.0);
		component.setBorder(null);
		container.getContentPane().add(component, BorderLayout.CENTER);

		((JComponent)container.getContentPane()).revalidate();
		((JComponent)container.getContentPane()).repaint();
	}

	public void disable() {
		JFrame container = (JFrame) parent.getComponent();

		
		if (component != null) {
			component.remove(scrollback.getComponent());
			component.remove(mainText.getComponent());
			container.getContentPane().remove(component);
			component = null;
		}
		container.getContentPane().add(mainText.getComponent(), BorderLayout.CENTER);
		
		((JComponent)container.getContentPane()).revalidate();
		((JComponent)container.getContentPane()).repaint();
	}
	
	public void setParent(ComponentWrapper parent) {
		this.parent = parent;
	}

	public void setScrollback(TextAreaWrapper scrollback) {
		this.scrollback = scrollback;
	}

	public void setMainText(TextAreaWrapper mainText) {
		this.mainText = mainText;
	}

}

