package gui3.text;

import gui3.CenterPanel;

public class ScrollbackController {
	private boolean isEnabled;
	private CenterPanel centerPanel;
	private TextAreaWrapper scrollback;
	private boolean isActionable;
	private ScrollbarWrapper scrollbarWrapper;
	
	public void init() {
		isActionable = true;
		if (isEnabled) {
			centerPanel.enable();
		} else {
			centerPanel.disable();
		}
	}
	
	public void pageUp() {
		setEnabled(true);
		TextView textArea = (TextView)scrollback.getComponent();
		textArea.onPageUp();
	}
	
	public void pageDown() {
		TextView textArea = (TextView)scrollback.getComponent();
		textArea.onPageDown();
		if (textArea.getLineIndex() <= 0) {
			setEnabled(false);
		}
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		if (isActionable && isEnabled != this.isEnabled) {
			if (isEnabled) {
				centerPanel.enable();
				TextView textArea = (TextView)scrollback.getComponent();
				textArea.setLineIndex(0);
				textArea.repaint();
			} else {
				centerPanel.disable();
				scrollbarWrapper.resetToBottom();
			}
		}
		this.isEnabled = isEnabled;
	}

	public void setCenterPanel(CenterPanel centerPanel) {
		this.centerPanel = centerPanel;
	}

	public void setScrollback(TextAreaWrapper scrollback) {
		this.scrollback = scrollback;
	}

	public void setScrollbarWrapper(ScrollbarWrapper scrollbarWrapper) {
		this.scrollbarWrapper = scrollbarWrapper;
	}

	public void move(int wheelRotation) {
		TextView textArea = (TextView)scrollback.getComponent();
		textArea.onMove(wheelRotation);
		setEnabled(textArea.getLineIndex() > 0);
	}
}
