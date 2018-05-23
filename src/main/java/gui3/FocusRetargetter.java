package gui3;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class FocusRetargetter implements FocusListener {

	private ComponentWrapper from;
	private ComponentWrapper to;

	public void setFrom(ComponentWrapper from) {
		this.from = from;
	}

	public void setTo(ComponentWrapper to) {
		this.to = to;
	}

	public void init() {
		from.getComponent().addFocusListener(this);
	}

	public void focusGained(FocusEvent arg0) {
		to.getComponent().requestFocus();
	}

	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
