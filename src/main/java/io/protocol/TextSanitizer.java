package io.protocol;

import io.listener.CodeListener;
import io.listener.TextListener;

public interface TextSanitizer extends TextListener, CodeListener {

	void addTextListener(TextListener textListener);
	
}
