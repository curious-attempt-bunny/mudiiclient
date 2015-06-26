package io.protocol.impl;

import io.listener.BytesListener;
import io.protocol.TelnetProtocolHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Vector;

public class BasicTelnetProtocolHandler implements TelnetProtocolHandler {

	private final int STATE_RAW = 0;
	private final int STATE_IAC = 1;
	private final int STATE_DO = 2;
	private final int STATE_WILL = 3;
	private final int STATE_SWALLOW_NEXT = 4;
	
	private Vector bytesListeners;
	private int state = STATE_RAW;
	private OutputStream outputStream;
	
	public BasicTelnetProtocolHandler() {
		bytesListeners = new Vector();
	}

	public void addBytesListener(BytesListener bytesListener) {
		bytesListeners.add(bytesListener);
	}

	// optimized processing of byte array
	public void onBytes(byte[] bytes, int offset, int length) {
		// disabled optimization for testing
		ByteArrayOutputStream byteArrayOutputStream = //null;
			new ByteArrayOutputStream();
		for(int i=offset; i<offset+length; i++) {
			boolean swallowByte;
			try {
				swallowByte = processByte(bytes[i]);
			} catch (IOException e) {
				e.printStackTrace();
				swallowByte = false; // TODO review
//				throw new RuntimeException("Socket closed?");
			}
			
			if (swallowByte) {
				// first swallowed byte
				if (byteArrayOutputStream == null) {
					byteArrayOutputStream = new ByteArrayOutputStream();
					// catch-up
					byteArrayOutputStream.write(bytes, offset, i-offset);
				}
			} else {
				if (byteArrayOutputStream != null) {
					byteArrayOutputStream.write(bytes[i]);
				}
			}
		}
		
		if (byteArrayOutputStream == null) {
			fireOnBytes(bytes, offset, length);
		} else {
			if (byteArrayOutputStream.size() > 0) {
				byte[] bytesRemaining = byteArrayOutputStream.toByteArray();
				fireOnBytes(bytesRemaining, 0, bytesRemaining.length);
			}
		}
	}

	private boolean processByte(byte b) throws IOException {
		boolean swallowByte = true;
		
//		System.out.println("State: "+state+" byte="+(int)(char)b);
		switch(state) {
		case STATE_RAW:
			if (b == BYTE_IAC) {
				state = STATE_IAC;
			} else {
				swallowByte = false;
			}
			break;
			
		case STATE_IAC:
			if (b == BYTE_IAC) {
				state = STATE_RAW;
				swallowByte = false;
			} else if (b == BYTE_DO) {
				state = STATE_DO;
			} else if (b == BYTE_WILL) {
				state = STATE_WILL;
			} else {
				// illegal state
				state = STATE_SWALLOW_NEXT;
			}
			break;
			
		case STATE_DO:
//			System.out.println("DO "+(int)b);
			outputStream.write(new byte[] { BYTE_IAC, BYTE_WONT, b});
			outputStream.flush();
			state = STATE_RAW;
			break;

		case STATE_WILL:
//			System.out.println("WILL "+(int)b);
			outputStream.write(new byte[] { BYTE_IAC, BYTE_DONT, b});
			outputStream.flush();
			state = STATE_RAW;
			break;
			
		case STATE_SWALLOW_NEXT:
			state = STATE_RAW;
		}
		
		if (b == 0 || b == 1) {
			// NOTE this is not kosha, but these chars kill StyledText
			swallowByte = true;
		}
		return swallowByte;
	}

	private void fireOnBytes(byte[] bytes, int offset, int length) {
		Iterator it = bytesListeners.iterator();
		while (it.hasNext()) {
			BytesListener bytesListener = (BytesListener) it.next();
			
			bytesListener.onBytes(bytes, offset, length);
		}
	}

	public void setOutputStream(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

}
