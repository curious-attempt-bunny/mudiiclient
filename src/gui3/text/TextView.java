package gui3.text;

import gui3.ViewListener;

public interface TextView {
	void addViewListener(ViewListener viewListener);

	int getPageSize();

	void onPageDown();

	void onPageUp();

	int getLineIndex();

	void setLineIndex(int i);

	void repaint();
}
