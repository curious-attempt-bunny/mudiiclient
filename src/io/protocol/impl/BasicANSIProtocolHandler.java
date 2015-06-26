package io.protocol.impl;

import io.listener.BytesListener;
import io.listener.StyleListener;
import io.protocol.ANSIProtocolHandler;

import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Vector;

import domain.Style;

// http://en.wikipedia.org/wiki/ANSI_color

public class BasicANSIProtocolHandler implements ANSIProtocolHandler {

	private final byte BYTE_ESC = (byte) 27;

	private final int STATE_RAW = 0;

	private final int STATE_ESC = 1;

	private final int STATE_ANSI = 2;

	private Vector bytesListeners;

	private int state = STATE_RAW;

	private StringBuffer ansi;

	private Vector styleListeners;

	public BasicANSIProtocolHandler() {
		bytesListeners = new Vector();
		styleListeners = new Vector();

		ansi = new StringBuffer();
	}

	public void addBytesListener(BytesListener bytesListener) {
		bytesListeners.add(bytesListener);
	}

	private void fireOnBytes(byte[] bytes, int offset, int length) {
//		System.out.println("Text: "+new String(bytes, offset, length));
		
		Iterator it = bytesListeners.iterator();
		while (it.hasNext()) {
			BytesListener bytesListener = (BytesListener) it.next();

			bytesListener.onBytes(bytes, offset, length);
		}
	}

	public void onBytes(byte[] bytes, int offset, int length) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		for (int i = offset; i < offset + length; i++) {
			boolean swallowByte = true;
			byte b = bytes[i];
//			System.out.println((char)b+" ("+(int)b+")");
			// System.out.println("State: "+state+" byte="+(int)(char)b);
			switch (state) {
			case STATE_RAW:
				if (b == BYTE_ESC) {
					state = STATE_ESC;
				} else {
					swallowByte = false;
				}
				break;

			case STATE_ESC:
				if (b == '[' || b == '-') {
					state = STATE_ANSI;
				} else {
					// should strickly un-swallow the ESC
					state = STATE_RAW;
					swallowByte = false;
				}
				break;

			case STATE_ANSI:
				ansi.append((char) b);
				if (Character.isLetter(b)) {
//					System.out.println(ansi.toString());
					if (byteArrayOutputStream.size() > 0) {
						byte[] bytesRemaining = byteArrayOutputStream
								.toByteArray();
						fireOnBytes(bytesRemaining, 0, bytesRemaining.length);
						byteArrayOutputStream = new ByteArrayOutputStream();
					}
					processAnsi(ansi.toString());
					ansi.delete(0, ansi.length());
					state = STATE_RAW;
				}
				break;
			}

			if (!swallowByte) {
				byteArrayOutputStream.write(bytes[i]);
			}
		}

		if (byteArrayOutputStream.size() > 0) {
			byte[] bytesRemaining = byteArrayOutputStream.toByteArray();
			fireOnBytes(bytesRemaining, 0, bytesRemaining.length);
		}
	}

	private boolean processAnsi(String ansi) {
		if (ansi.endsWith("m")) {
			boolean bold = false;
			int fg = 7;
			int bg = 0;

			String elements[] = ansi.substring(0, ansi.length() - 1).split(";");

			for (int i = 0; i < elements.length; i++) {
				try {
					int val = Integer.parseInt(elements[i].trim());
					if (val == 1) {
						bold = true;
					} else if (val >= 30 && val <= 37) {
						fg = val - 30;
					} else if (val >= 40 && val < 47) {
						bg = val - 40;
					}
				} catch (NumberFormatException e) {
					// TODO
					e.printStackTrace();
				}
			}

//			System.out.println("Style: bg: "+bg+" fg: "+fg+" (ansi: "+ansi+")");

			fireOnStyle(new Style(fg, bg, bold));

			return true;
		} else {
			return false;
		}
	}

	public void addStyleListener(StyleListener styleListener) {
		styleListeners.add(styleListener);
	}

	private void fireOnStyle(Style style) {
		Iterator it = styleListeners.iterator();
		while (it.hasNext()) {
			StyleListener styleListener = (StyleListener) it.next();

			styleListener.onStyle(style);
		}
	}

}
