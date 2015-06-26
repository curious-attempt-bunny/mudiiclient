package gui3.text;

import io.listener.CodeListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import backend2.OutputListener;
import domain.Style;

public class SnoopHandler implements OutputListener, CodeListener {
	private List outputListeners;
	private Map mapIdToPrefix;
	private int nesting;
	private List prefixListeners;
	private TextAreaDocumentPrefix nextPrefix;
	private boolean isNextPrefix;
	
	public SnoopHandler() {
		outputListeners = new Vector();
		prefixListeners = new Vector();
		mapIdToPrefix = new HashMap();
		nesting = 0;
		isNextPrefix = false;
	}

	public void onOutputEnd() {
		Iterator it = outputListeners.iterator();
		while (it.hasNext()) {
			OutputListener outputListener = (OutputListener) it.next();
			outputListener.onOutputEnd();
		}
	}

	public void onOutputStart() {
		Iterator it = outputListeners.iterator();
		while (it.hasNext()) {
			OutputListener outputListener = (OutputListener) it.next();
			outputListener.onOutputStart();
		}
	}

	public void onOutput(String text) {
		if (isNextPrefix) {
			fireOnPrefix(nextPrefix);
			nextPrefix = null;
			isNextPrefix = false;
		}
		
		Iterator it = outputListeners.iterator();
		while (it.hasNext()) {
			OutputListener outputListener = (OutputListener) it.next();
			outputListener.onOutput(text);
		}
	}
	
	public void onCode(String code) {
		if (code.startsWith("<94")) {
			String id = code.substring(3,7);
			int number = Integer.parseInt(code.substring(8,9));
			int bg = (number % 2 == 1 ? Style.COLOUR_BLUE : Style.COLOUR_MAGENTA);
			TextAreaDocumentPrefix areaDocumentPrefix = new TextAreaDocumentPrefix(new Style(Style.COLOUR_BLACK, bg, false), "|"+number+"|");
			mapIdToPrefix.put(id, areaDocumentPrefix);
		}
		
		if (code.startsWith("<97")) {
			String id = code.substring(3,7);
			TextAreaDocumentPrefix areaDocumentPrefix = (TextAreaDocumentPrefix) mapIdToPrefix.get(id);
			setPrefix(areaDocumentPrefix);
			
			nesting = 1;
		} else if (nesting > 0) {
			if (code.equals("<>")) {
				nesting--;
			} else {
				nesting++;
			}
			
			if (nesting == 0) {
				setPrefix(null);
			}
		}
	}

	private void setPrefix(TextAreaDocumentPrefix areaDocumentPrefix) {
		nextPrefix = areaDocumentPrefix;
		isNextPrefix = true;
	}

	private void fireOnPrefix(TextAreaDocumentPrefix areaDocumentPrefix) {
		Iterator it = prefixListeners.iterator();
		while (it.hasNext()) {
			PrefixListener prefixListener = (PrefixListener) it.next();
			prefixListener.onPrefix(areaDocumentPrefix);
		}
	}

	public void addOutputListener(OutputListener outputListener) {
		outputListeners.add(outputListener);
	}
	
	public void addPrefixListener(PrefixListener prefixListener) {
		prefixListeners.add(prefixListener);
	}
}
