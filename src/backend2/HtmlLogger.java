package backend2;

import gui3.ColourHelper;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import domain.Style;

public class HtmlLogger implements Logger, Runnable {

	private String filename;
	private BufferedWriter bufferedWriter;
	private ColourHelper colourHelper;
	private Style style;
	private char[] colourNames;
	private List appends;
	
	public void init() {
		appends = new ArrayList();
		colourNames = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p' };
		
		append("<html>\r");
		append("<head>\r");
//		append("<link rel=\"stylesheet\" href=\"colours.css\" type=\"text/css\">");
//		append("<body bgcolor=\"#000000\"><pre><font>\r");
		append("<style type=\"text/css\">\r");
		for(int f=0; f<16; f++) {
			Color fc = colourHelper.get(f);
			for(int b=0; b<16; b++) {
				Color bc = colourHelper.get(b);
				append("span."+colourNames[f]+colourNames[b]);
				append(" {\r");
				append("  background: #");
				append(getColorEncoding(bc.getRGB()));
				append(" !important;\r  color: #");
				append(getColorEncoding(fc.getRGB()));
				append(" !important;\r}\r");
			}
		}
		append("BODY {\r  font-family: monospace\r}\r");
		append("</style>\r");
		append("</head>\r");
		append("<body bgcolor=\"#000000\">\r");
//		append("<PRE>\r");
		append("<SPAN class=\""+colourNames[Style.COLOUR_LT_WHITE]+colourNames[Style.COLOUR_BLACK]+"\">\r");
		
		new Thread(this).start();
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void onText(String text) {
		String[] lines = text.split("\r");
		for (int i=0; i<lines.length; i++) {
			if (i!=0) {
				append("<br>");
			}
			append(lines[i]);
		}
	}

	private void append(String text) {
		if (style != null) {
			Style style2 = style;
			style = null;
			append("</SPAN><SPAN CLASS=\""+colourNames[style2.getForegroundColour()]+colourNames[style2.getBackgroundColour()]+"\">");
		}
		synchronized (appends) {
			appends.add(text);
			appends.notifyAll();
		}
	}

	public void onStyle(Style style) {
		this.style = style;
	}

	private String getColorEncoding(int rgb) {
		String str = Integer.toHexString(rgb & 0xFFFFFF);
		while(str.length() < 6) {
			str = "0" + str;
		}
		return str;
	}

	public void setColourHelper(ColourHelper colourHelper) {
		this.colourHelper = colourHelper;
	}

	public void onCode(String code) {
		append("<!--{"+code.substring(1,code.length()-1)+"}-->");
	}

	public void run() {
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(filename));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List items = new ArrayList();
		while(true) {
			synchronized(appends) {
				if (appends.isEmpty()) {
					try {
						appends.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
				}
				items.addAll(appends);
				appends.clear();
			}
			try {
				Iterator it = items.iterator();
				while(it.hasNext()) {
					String item = (String)it.next();
					bufferedWriter.write(item);
				}
				bufferedWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
//				break;
			}
			items.clear();
		}
	}

}
