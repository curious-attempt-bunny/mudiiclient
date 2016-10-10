package gui3.text;

import domain.Configuration;
import gui3.CenterPanel;
import gui3.ColourHelper;
import gui3.ComponentWrapper;
import gui3.FontConsumer;
import io.listener.CodeListener;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import backend2.OutputListener;
import domain.Style;
import io.protocol.impl.BasicANSIProtocolHandler;
import io.protocol.impl.BasicMudClientModeStyle;

import javax.swing.*;

public class SnoopHandler implements OutputListener, CodeListener, FontConsumer {
	private Map mapPrefixToListeners = new HashMap();
	private Map mapIdToPrefix;
	private int nesting;
	private List prefixListeners;
	private TextAreaDocumentPrefix nextPrefix;
	private boolean isNextPrefix;
	private String currentPrefix;
	private Configuration configuration;
	private ColourHelper colourHelper;
	private Font font;
	private BasicMudClientModeStyle mudClientModeStyle;
	private BasicANSIProtocolHandler ansiProtocolHandler;

	public SnoopHandler() {
		mapPrefixToListeners.put(null, new Vector());
		prefixListeners = new Vector();
		mapIdToPrefix = new HashMap();
		nesting = 0;
		isNextPrefix = false;
	}

	public void onOutputEnd() {
		Iterator it = ((List)mapPrefixToListeners.get(currentPrefix)).iterator();
		while (it.hasNext()) {
			OutputListener outputListener = (OutputListener) it.next();
			outputListener.onOutputEnd();
		}
	}

	public void onOutputStart() {
		Iterator it = ((List)mapPrefixToListeners.get(currentPrefix)).iterator();
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
		
		Iterator it = ((List)mapPrefixToListeners.get(currentPrefix)).iterator();
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
		if (areaDocumentPrefix == null) {
			currentPrefix = null;
		} else {
			currentPrefix = areaDocumentPrefix.text;
			if (!mapPrefixToListeners.containsKey(currentPrefix)) {
				Vector listeners = new Vector();

				final JFrame frame = new JFrame();

				BetterTextAreaDocument textDocument = new BetterTextAreaDocument();

				TextAreaWrapper textAreaWrapper = new TextAreaWrapper();
				textAreaWrapper.setDocument(textDocument);
				textAreaWrapper.setConfiguration(configuration);
				textAreaWrapper.setColourHelper(colourHelper);
				ansiProtocolHandler.addStyleListener(textAreaWrapper);
				mudClientModeStyle.addStyleListener(textAreaWrapper);

				CenterPanel centerPanel = new CenterPanel();

				TextAreaWrapper scrollback = new TextAreaWrapper();
				ScrollbarWrapper scrollbarWrapper = new ScrollbarWrapper();
				ScrollbackController scrollbackController = new ScrollbackController();
				scrollback.setScrollbackController(scrollbackController);
				textAreaWrapper.setScrollbackController(scrollbackController);
				scrollback.setScrollback(true);
				scrollback.setDocument(textDocument);
				scrollback.setColourHelper(colourHelper);
				scrollback.setConfiguration(configuration);
				scrollbackController.setCenterPanel(centerPanel);
				scrollbackController.setEnabled(false);
				scrollbackController.setScrollback(scrollback);
				scrollbackController.setScrollbarWrapper(scrollbarWrapper);
				scrollbarWrapper.setScrollback(scrollback);
				scrollbarWrapper.setScrollbackController(scrollbackController);

				centerPanel.setParent(new ComponentWrapper() {
					public void init() {

					}

					public Component getComponent() {
						return frame;
					}
				});
				centerPanel.setScrollback(scrollback);
				centerPanel.setMainText(textAreaWrapper);

				textAreaWrapper.init();
				textAreaWrapper.setFont(font);
				scrollback.init();
				scrollback.setFont(font);
				scrollbackController.init();
				scrollbarWrapper.init();
				centerPanel.init();




				listeners.add(textAreaWrapper);
				mapPrefixToListeners.put(currentPrefix, listeners);

				frame.getContentPane().add(centerPanel.getComponent());
				int x = configuration.getInt("x", 0);
				int y = configuration.getInt("y", 0);
				int width = configuration.getInt("sizeX", 1000);
				int height = configuration.getInt("sizeY", 700);
				frame.setLocation(x + (width / 2), y);
				frame.setSize(width / 2, height);
				frame.show();
			}
		}
	}

	public void addOutputListener(OutputListener outputListener) {
		((List)mapPrefixToListeners.get(null)).add(outputListener);
	}
	
	public void addPrefixListener(PrefixListener prefixListener) {
		prefixListeners.add(prefixListener);
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setColourHelper(ColourHelper colourHelper) {
		this.colourHelper = colourHelper;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public void setMudClientModeStyle(BasicMudClientModeStyle mudClientModeStyle) {
		this.mudClientModeStyle = mudClientModeStyle;
	}

	public void setAnsiProtocolHandler(BasicANSIProtocolHandler ansiProtocolHandler) {
		this.ansiProtocolHandler = ansiProtocolHandler;
	}
}
