package io.trigger;

import io.listener.TextListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Trigger implements TextListener {

	private Pattern pattern;
//	private final boolean multipleMatches;
	private boolean doneMatching;
	private StringBuffer buffer;
	private final TriggerAction triggerAction;
	
	public Trigger(String pattern, TriggerAction triggerAction) { //, boolean multipleMatches) {
		this.triggerAction = triggerAction;
//		this.multipleMatches = multipleMatches;
		this.pattern = Pattern.compile(pattern, Pattern.DOTALL | Pattern.MULTILINE);
		doneMatching = false;
		buffer = new StringBuffer();
	}
	
	public void onText(String text) {
		if (!doneMatching) {
			buffer.append(text);
//			System.out.println("Testing: "+buffer.toString());
			Matcher matcher = pattern.matcher(buffer.toString());
			
			if (matcher.matches()) {
//				System.out.println("TRIGGER!");
				triggerAction.onTrigger();
				doneMatching = true; //!multipleMatches;
			} else {
				trimLines();
			}
			
		}
	}

	private void trimLines() {
		int pos = buffer.lastIndexOf("\n");
		if (pos != -1) {
			buffer.delete(0, pos+1);
		}
	}

}
