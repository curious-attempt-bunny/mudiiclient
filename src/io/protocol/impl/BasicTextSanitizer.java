package io.protocol.impl;

import io.listener.CodeListener;
import io.listener.TextListener;
import io.protocol.TextSanitizer;

import java.util.Iterator;
import java.util.Vector;

public class BasicTextSanitizer implements TextSanitizer {

	private Vector textListeners;
	private StringBuffer buffer;
	private Vector codeListeners;
	
	public BasicTextSanitizer() {
		textListeners = new Vector();
		codeListeners = new Vector();
		buffer = new StringBuffer();
	}
	
	public void onText(String text) {
		buffer.append(text);
	}

	public void onCode(String code) {
		if (buffer.length() > 0) {
			String text = buffer.toString();
			String[] lines = text.split("\r");
			for (int i = 0; i < lines.length; i++) {
				if (i < lines.length-1) {
					fireOnText(lines[i]+"\r");
				} else {
					fireOnText(lines[i]);
				}
			}
			buffer.delete(0, buffer.length());
		}
		fireOnCode(code);
	}

	private void fireOnCode(String code) {
		Iterator it = codeListeners.iterator();
	
		while (it.hasNext()) {
			CodeListener codeListener = (CodeListener) it.next();
			codeListener.onCode(code);
		}
	}
	
	public void addTextListener(TextListener textListener) {
		textListeners.add(textListener);
	}
	
	private void fireOnText(String text) {
		Iterator it = textListeners.iterator();
	
		while (it.hasNext()) {
			TextListener textListener = (TextListener) it.next();
			textListener.onText(text);
		}
	}

	public void addCodeListener(CodeListener codeListener) {
		codeListeners.add(codeListener);
	}

}
