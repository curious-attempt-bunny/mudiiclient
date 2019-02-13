package gui3;

import domain.Configuration;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FontManager {

	private List fontConsumers;
	private int fontSize;
	private Configuration configuration;
	private String fontName;

	public FontManager() {
		fontConsumers = new Vector();
	}
	
	public void init() {
		fontName = "Monospaced";

		Iterator iterator = getFontNames().iterator();
		while(iterator.hasNext()) {
			String name = (String) iterator.next();
			if (name.equalsIgnoreCase("Courier")) {
				fontName = name;
				break;
			}
		}

		if (configuration.getSetting("fontSize") == null) {
			autoselectFontSize();
		}
		fontSize = configuration.getInt("fontSize", 14);
		fireFont(new Font(getFontName(), getFontStyle(), fontSize));
	}

	private void autoselectFontSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int bestFontSize = 14;
		int fontSize = 14;
		while(fontSize <= 24) {
			FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(new Font(getFontName(), getFontStyle(), fontSize));
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

	private int getFontStyle() {
		return Font.BOLD; //Font.PLAIN;
	}

	private void fireFont(Font font) {
		Iterator it = fontConsumers.iterator();
		while (it.hasNext()) {
			FontConsumer fontConsumer = (FontConsumer) it.next();
			
			fontConsumer.setFont(font);
		}
	}

	public void onFontBigger() {
		if (fontSize < 24) {
			fontSize += 2;
			configuration.setInt("fontSize", fontSize);
			fireFont(new Font(getFontName(), getFontStyle(), fontSize));
		}
	}
	
	public void onFontSmaller() {
		if (fontSize > 8) {
			fontSize -= 2;
			configuration.setInt("fontSize", fontSize);
			fireFont(new Font(getFontName(), getFontStyle(), fontSize));
		}
	}

	public void addFontConsumer(FontConsumer fontConsumer) {
		fontConsumers.add(fontConsumer);
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public List getFontNames() {
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
		List fontNames = new ArrayList();
		final Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
		final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

		for(int i=0; i<fonts.length; i++) {
			final int index = i;
			executor.execute(new Runnable() {
				@Override
				public void run() {
					Font font = fonts[index];

					FontMetrics fontMetrics = toolkit.getFontMetrics(new Font(font.getName(), Font.PLAIN, 48));
					if (fontMetrics.charWidth('m') == fontMetrics.charWidth('i')) {
//						System.out.println(font.getName() + ": A(" + fontMetrics.charWidth('A') + ") m(" + fontMetrics.charWidth('m') + ") i(" + fontMetrics.charWidth('i') + ")");
						fontNames.add(font.getName());
					}
				}
			});
		}
		executor.shutdown();

		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// Don't allow font testing to proceed longer than necessary
		}

		return fontNames;
	}

	private String getFontName() {
		return fontName;
	}
}
