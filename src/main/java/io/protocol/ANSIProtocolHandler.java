package io.protocol;

import io.listener.BytesListener;
import io.listener.StyleListener;

public interface ANSIProtocolHandler extends BytesListener {
	void addBytesListener(BytesListener bytesListener);
	void addStyleListener(StyleListener styleListener);
}
