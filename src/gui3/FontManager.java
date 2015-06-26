package gui3;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import domain.Configuration;

public class FontManager {

	private static final String FONT_NAME = "Monospaced";
//	private Font font;
	private List fontConsumers;
	private int fontSize;
	private Configuration configuration;

	public FontManager() {
		fontConsumers = new Vector();
	}
	
	public void init() {
		if (configuration.getSetting("fontSize") == null) {
			autoselectFontSize();
		}
		fontSize = configuration.getInt("fontSize", 14);
		fireFont(new Font(FONT_NAME, Font.PLAIN, fontSize));
	}

	private void autoselectFontSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int bestFontSize = 14;
		int fontSize = 14;
		while(fontSize <= 24) {
			FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(new Font(FONT_NAME, Font.PLAIN, fontSize));
			int sizex = screenSize.width / fontMetrics.charWidth('A');
			if (sizex >= 120) {
				bestFontSize = fontSize;
			} else {
				break;
			}
			fontSize += 2;
		}
		
		configuration.setInt("fontSize", bestFontSize);
	}

	private void fireFont(Font font) {
		Iterator it = fontConsumers.iterator();
		while (it.hasNext()) {
			FontConsumer fontConsumer = (FontConsumer) it.next();
			
			fontConsumer.setFont(font);
		}
//		this.font = font;
	}

	public void onFontBigger() {
		if (fontSize < 24) {
			fontSize += 2;
			configuration.setInt("fontSize", fontSize);
			fireFont(new Font(FONT_NAME, Font.PLAIN, fontSize));
		}
	}
	
	public void onFontSmaller() {
		if (fontSize > 8) {
			fontSize -= 2;
			configuration.setInt("fontSize", fontSize);
			fireFont(new Font(FONT_NAME, Font.PLAIN, fontSize));
		}
	}

	public void addFontConsumer(FontConsumer fontConsumer) {
		fontConsumers.add(fontConsumer);
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

}
