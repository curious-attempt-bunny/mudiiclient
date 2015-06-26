package io.protocol.impl;

import io.listener.CodeListener;
import io.listener.TextListener;
import io.protocol.MudClientFilter;

import java.util.Iterator;
import java.util.Vector;

public class BasicMudClientFilter implements MudClientFilter {

	private static final String CODE_SNOOP_INFO = "<94";
	private Vector codeListeners;
	private Vector textListeners;
	private int nesting;
	private boolean isHideWhileNesting;
	private boolean isLastPrompt;
	
	public BasicMudClientFilter() {
		textListeners = new Vector();
		codeListeners = new Vector();
		
		nesting = 0;
		isHideWhileNesting = true;
		isLastPrompt = false;
	}

	public void addCodeListener(CodeListener codeListener) {
		codeListeners.add(codeListener);
	}

	public void addTextListener(TextListener textListener) {
		textListeners.add(textListener);
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

	public void onCode(String code) {
		boolean fireCode = true;
		
		if (nesting > 0) {
			if (code.equals("<>")) {
				nesting--;
			} else {
				nesting++;
			}
			fireCode = !isHideWhileNesting;
		} else if (code.equals("<95>") || (code.startsWith("<1208") && !code.equals("<120806>")) || code.startsWith("<940112") || code.startsWith(CODE_SNOOP_INFO)) {
			nesting = 1;
			isHideWhileNesting = true;
			fireCode = false;
		} else if (code.equals("<01>")) {
			isHideWhileNesting = isLastPrompt;
			isLastPrompt = true;
			nesting = 1;
		} else {
			isLastPrompt = false;
		}
		
		if (fireCode) {
			fireOnCode(code);
		}
		
		
	}

	public void onText(String text) {
//		synchronized (this) {
			if (nesting == 0 || !isHideWhileNesting) {
				fireOnText(text);
			}
			
			if (nesting == 0 && isLastPrompt && text.trim().length() > 0) {
				isLastPrompt = false;
			}
//		}
	}



}
