package io.protocol;

import io.listener.BytesListener;

import java.io.OutputStream;

public interface TelnetProtocolHandler extends BytesListener {
	byte BYTE_IAC = (byte)255;
	byte BYTE_DONT = (byte)254;
	byte BYTE_DO = (byte)253;
	byte BYTE_WONT = (byte)252;
	byte BYTE_WILL = (byte)251;
	
	void addBytesListener(BytesListener bytesListener);

	void setOutputStream(OutputStream outputStream);
}
