package gui3.text;

import gui3.ComponentWrapper;
import gui3.DocumentListener;
import gui3.ViewListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

public class ScrollbarWrapper implements ComponentWrapper, DocumentListener, ViewListener, AdjustmentListener {

	private JScrollBar component;
//	private ComponentWrapper parent;
	private ComponentWrapper scrollback;
	private ScrollbackController scrollbackController;
	private boolean isUserAdjustment;
	
	public Component getComponent() {
		return component;
	}

	public void init() {
		component = new JScrollBar(JScrollBar.VERTICAL);
		component.setForeground(new Color(128,128,128));
		component.setBackground(new Color(64,64,64));
		component.setMinimum(0);
		component.setMaximum(1);
		component.setValue(1);
		component.setUnitIncrement(1);
		component.setBlockIncrement(15);
//		Container container = ((JFrame) parent.getComponent()).getContentPane();
//		container.add(component, BorderLayout.EAST);
		
		((TextView)scrollback.getComponent()).addViewListener(this);
		
		component.addAdjustmentListener(this);
		isUserAdjustment = true;
	}

//	public void setParent(ComponentWrapper parent) {
//		this.parent = parent;
//	}

	public void onNewDocumentLines(int linesAdded, int linesCount) {
		isUserAdjustment = false;
		component.setMaximum(linesCount);
		component.setValue(component.getValue()+linesAdded);
		isUserAdjustment = true;
	}

	public void setScrollback(TextAreaWrapper scrollback) {
		this.scrollback = scrollback;
	}

	public void onViewLineChange(int lineIndex) {
		isUserAdjustment = false;
		component.setValue(component.getMaximum()-lineIndex);
		component.setBlockIncrement(((TextView)scrollback.getComponent()).getPageSize());
		isUserAdjustment = true;
	}

	public void adjustmentValueChanged(AdjustmentEvent arg0) {
		if (isUserAdjustment) {
			if (component.getValue() >= component.getMaximum()-1) {
				scrollbackController.setEnabled(false);
			} else {
				scrollbackController.setEnabled(true);
			}
			((TextView)scrollback.getComponent()).setLineIndex(component.getMaximum() - component.getValue());
			scrollback.getComponent().repaint();
		}
	}

	public void setScrollbackController(ScrollbackController scrollbackController) {
		this.scrollbackController = scrollbackController;
	}

	public void resetToBottom() {
		component.setValue(component.getMaximum()-1);
	}

}
