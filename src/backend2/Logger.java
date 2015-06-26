package backend2;

import gui3.ColourHelper;
import io.listener.CodeListener;
import io.listener.StyleListener;
import io.listener.TextListener;

public interface Logger extends TextListener, StyleListener, CodeListener {
	void setFilename(String filename);
	void setColourHelper(ColourHelper colourHelper);
	void init();
}
