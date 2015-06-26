package io.protocol.impl;

import io.listener.CodeListener;
import io.listener.TextListener;
import io.protocol.MudClientProtocolHandler;

import java.util.Iterator;
import java.util.Vector;

public class BetterMudClientProtocolHandler implements MudClientProtocolHandler {
	private byte[] buffer;

	private int size;

	private Vector textListeners;

	private Vector codeListeners;

	private boolean isBufferCode;

	private boolean isLast13;
	
	public BetterMudClientProtocolHandler() {
		isBufferCode = false;
		size = 0;
		isLast13 = false;
		buffer = new byte[1024];
		textListeners = new Vector();
		codeListeners = new Vector();
	}

	public void addCodeListener(CodeListener codeListener) {
		codeListeners.add(codeListener);
	}

	public void addTextListener(TextListener textListener) {
		textListeners.add(textListener);
	}

	// 0-154: add to text
	// 155-255: add to code (flush previous text, 255: process code)

	public void onBytes(byte[] bytes, int offset, int length) {
		for (int i = offset; i < offset + length; i++) {
			onByte(bytes[i]);
		}

		if (!isBufferCode && size > 0) {
			fireOnText(flushBytesAsString());
		}
	}

	private void onByte(byte b) {
		boolean isCode = convert(b) >= 155;
		boolean isCodeEnd = convert(b) == 255;

		if (!isCode) {
//			if (isBufferCode) {
//				// TODO review
//				System.out.println("ALERT");
//				System.exit(0);
//			}
			if (isLast13 && b == 10) {
				addByte((byte)13);
			}
			if (b == 13) {
				isLast13 = true;
			} else {
				addByte(b);
			}
		} else {
			if (!isBufferCode && size > 0) {
				fireOnText(flushBytesAsString());
			}
			addByte(b);
			isBufferCode = true;
			if (isCodeEnd) {
				fireOnCode(getCode(buffer, 0, size));
				size = 0;
				isBufferCode = false;
			}
		}
	}

	private String flushBytesAsString() {
		String string = new String(buffer, 0, size);
		size = 0;
		isBufferCode = false;
//		isLast13 = false;
		return string;
	}

	private void addByte(byte b) {
		if (size == buffer.length) {
			byte[] buffer2 = new byte[buffer.length*2];
			for(int i=0; i<buffer.length; i++) {
				buffer2[i] = buffer[i];
			}
			buffer = buffer2;
		}
		buffer[size++] = b;
	}

	private String getCode(byte[] bytes, int start, int length) {
		StringBuffer buf = new StringBuffer();

		buf.append('<');
		for (int i = start; i < start + length - 1; i++) {
			int val = convert(bytes[i]) - 155;
			if (val < 10) {
				buf.append('0');
			}
			buf.append(Integer.toString(val));
		}
		buf.append('>');
		return buf.toString();
	}

	private void fireOnCode(String code) {
//		System.out.print(code);
		Iterator it = codeListeners.iterator();

		while (it.hasNext()) {
			CodeListener codeListener = (CodeListener) it.next();
			codeListener.onCode(code);
		}
	}

	private void fireOnText(String text) {
//		System.out.print(text);
		Iterator it = textListeners.iterator();

		while (it.hasNext()) {
			TextListener textListener = (TextListener) it.next();
			textListener.onText(text);
		}
	}

	private int convert(byte b) {
		return b & (255 - 128) + (b < 0 ? 128 : 0);
	}
}
