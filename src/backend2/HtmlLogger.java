package backend2;

import domain.State;
import gui3.ColourHelper;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import domain.Style;
import gui3.Launcher;
import io.listener.StateListener;

public class HtmlLogger implements Logger, Runnable, StateListener {

	private String filename;
	private BufferedWriter bufferedWriter;
	private ColourHelper colourHelper;
	private Style style;
	private char[] colourNames;
	private List appends;
	private String nextCode;
	private long lastTimestamp = 0;
	private Map state = new HashMap();;
	private Map newState = new HashMap();;

	public void init() {
		appends = new ArrayList();
		colourNames = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p' };

		append("<html>\r");
		append("<head>\r");
		append("<link rel=\"stylesheet\" href=\"http://mud2.net/log/style.css\" type=\"text/css\">");
		append("<script src=\"http://mud2.net/log/script.js?v=" + URLEncoder.encode(Launcher.VERSION) + "\"></script>");

//		append("<style type=\"text/css\">\r");
//		for(int f=0; f<16; f++) {
//			Color fc = colourHelper.get(f);
//			for(int b=0; b<16; b++) {
//				Color bc = colourHelper.get(b);
//				append("span."+colourNames[f]+colourNames[b]);
//				append(" {\r");
//				append("  background: #");
//				append(getColorEncoding(bc.getRGB()));
//				append(" !important;\r  color: #");
//				append(getColorEncoding(fc.getRGB()));
//				append(" !important;\r}\r");
//			}
//		}
//		append("BODY {\r  font-family: monospace\r}\r");
//		append("</style>\r");
		append("</head>\r");
		append("<body style=\"background-color: #000; color: #fff; font-family: monospace;\">\r");
//		append("<PRE>\r");
		append("<SPAN class=\""+colourNames[Style.COLOUR_LT_WHITE]+colourNames[Style.COLOUR_BLACK]+"\">\r");

		new Thread(this).start();
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void onText(String text) {
		text = text.replaceAll("\t", "        "); // not accurate
		if (text.indexOf("  ") >= 0 || text.matches("\\s* .*")) {
			text = text.replaceAll(" ", "&nbsp;");
		}

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
			String codeHint = "";
			if (nextCode != null) {
				codeHint = " "+nextCode;
				nextCode = null;

				long now = System.currentTimeMillis();
				if (now >= lastTimestamp + 2000) {
					codeHint += "\" t=\""+(now/1000);
					lastTimestamp = now;
				}

				Iterator iterator = newState.entrySet().iterator();
				while(iterator.hasNext()) {
					Map.Entry entry = (Map.Entry) iterator.next();
					codeHint += "\" "+entry.getKey()+"=\""+entry.getValue();
				}
				newState.clear();
			}
			append("</SPAN><SPAN CLASS=\"" + colourNames[style2.getForegroundColour()] + colourNames[style2.getBackgroundColour()] + codeHint + "\">");
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
		if (code.length() > 2) {
			nextCode = code.substring(1, code.length() - 1);
		}
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

//	@Override
	public void onState(String key, Object value) {
		if (key == State.KEY_MAGIC || key == State.KEY_POINTS || key == State.KEY_STAMINA || key == State.KEY_RESET_TIME) {
			if ((value == null && state.get(key) != null) ||
					(state.get(key) == null && value != null) ||
					!state.get(key).equals(value)) {
				newState.put(key, value);
			}
			state.put(key, value);
		}
	}
}
