package io.protocol.impl;

import io.listener.StyleListener;
import io.protocol.MudClientModeStyle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import domain.Style;

public class BasicMudClientModeStyle implements MudClientModeStyle {
	private Map mapCodeToStyle;
	private Vector styleListeners;

	public BasicMudClientModeStyle() {
		mapCodeToStyle = new HashMap();
		styleListeners = new Vector();
		addMapping("<00>", Style.COLOUR_LT_WHITE,Style.COLOUR_BLACK);
		addMapping("<0103>", Style.COLOUR_LT_BLUE,Style.COLOUR_BLACK);
		addMapping("<01", Style.COLOUR_BLUE,Style.COLOUR_BLACK);
		addMapping("<0200>", Style.COLOUR_BLACK,Style.COLOUR_GREEN);
		addMapping("<0201>", Style.COLOUR_LT_GREEN,Style.COLOUR_BLACK);
		addMapping("<0202>", Style.COLOUR_GREEN,Style.COLOUR_BLACK);
		addMapping("<0300", Style.COLOUR_GREEN,Style.COLOUR_BLACK);
		addMapping("<0301", Style.COLOUR_CYAN,Style.COLOUR_BLACK);
		addMapping("<03", Style.COLOUR_LT_CYAN,Style.COLOUR_BLACK);
		addMapping("<0400", Style.COLOUR_MAGENTA,Style.COLOUR_BLACK);
		addMapping("<0401", Style.COLOUR_LT_MAGENTA,Style.COLOUR_BLACK);
		addMapping("<050009>", Style.COLOUR_LT_YELLOW,Style.COLOUR_BLACK);
		addMapping("<0500", Style.COLOUR_RED,Style.COLOUR_BLACK);
		addMapping("<050109>", Style.COLOUR_LT_YELLOW,Style.COLOUR_BLACK);
		addMapping("<0501", Style.COLOUR_LT_RED,Style.COLOUR_BLACK);
		addMapping("<06", Style.COLOUR_LT_BLUE,Style.COLOUR_BLACK);
		addMapping("<07", Style.COLOUR_RED,Style.COLOUR_BLACK);
		addMapping("<0801>", Style.COLOUR_LT_RED,Style.COLOUR_BLACK);
		addMapping("<0803>", Style.COLOUR_LT_RED,Style.COLOUR_BLACK);
		addMapping("<08", Style.COLOUR_RED,Style.COLOUR_BLACK);
		addMapping("<0805>", Style.COLOUR_WHITE,Style.COLOUR_BLACK);
		addMapping("<0806>", Style.COLOUR_WHITE,Style.COLOUR_BLACK);
		addMapping("<0807>", Style.COLOUR_WHITE,Style.COLOUR_BLACK);
		addMapping("<0808>", Style.COLOUR_BLACK,Style.COLOUR_RED);
		addMapping("<0809>", Style.COLOUR_WHITE,Style.COLOUR_BLACK);
		addMapping("<0900>", Style.COLOUR_YELLOW,Style.COLOUR_BLACK);
		// shout
		//addMapping("<0901>", Style.COLOUR_LT_WHITE,Style.COLOUR_GREEN, true);
		addMapping("<0901>", Style.COLOUR_BLUE,Style.COLOUR_BLACK, true);
		// say
		addMapping("<0902>", Style.COLOUR_LT_YELLOW,Style.COLOUR_BLACK);
		// tell
//		addMapping("<0903>", Style.COLOUR_LT_WHITE,Style.COLOUR_BLUE, true);
		addMapping("<0903>", Style.COLOUR_LT_CYAN,Style.COLOUR_BLACK);
		addMapping("<09", Style.COLOUR_LT_YELLOW,Style.COLOUR_BLACK);
		addMapping("<1000>", Style.COLOUR_BLACK,Style.COLOUR_YELLOW);
		addMapping("<1003>", Style.COLOUR_LT_YELLOW,Style.COLOUR_YELLOW);
		addMapping("<11", Style.COLOUR_LT_RED,Style.COLOUR_BLACK);
		addMapping("<1204>", Style.COLOUR_GREEN,Style.COLOUR_BLACK);
		addMapping("<1205>", Style.COLOUR_GREEN,Style.COLOUR_BLACK);
		addMapping("<1206>", Style.COLOUR_YELLOW,Style.COLOUR_BLACK);
		addMapping("<1207>", Style.COLOUR_LT_YELLOW,Style.COLOUR_BLACK);
		addMapping("<12", Style.COLOUR_WHITE,Style.COLOUR_BLACK);
		addMapping("<13", Style.COLOUR_LT_BLACK,Style.COLOUR_WHITE);
		addMapping("<1401>", Style.COLOUR_LT_WHITE,Style.COLOUR_GREEN);
		addMapping("<140301>", Style.COLOUR_LT_WHITE,Style.COLOUR_GREEN);
		addMapping("<140303>", Style.COLOUR_LT_WHITE,Style.COLOUR_GREEN);
		addMapping("<140401>", Style.COLOUR_LT_WHITE,Style.COLOUR_GREEN);
		addMapping("<140403>", Style.COLOUR_LT_WHITE,Style.COLOUR_GREEN);
		addMapping("<140501>", Style.COLOUR_LT_WHITE,Style.COLOUR_GREEN);
		addMapping("<14", Style.COLOUR_GREEN,Style.COLOUR_BLACK);
		addMapping("<15", Style.COLOUR_BLACK,Style.COLOUR_CYAN);
		addMapping("<16", Style.COLOUR_LT_WHITE,Style.COLOUR_YELLOW);
		addMapping("<18", Style.COLOUR_WHITE,Style.COLOUR_BLACK);
		addMapping("<19", Style.COLOUR_LT_WHITE,Style.COLOUR_BLUE);
		addMapping("<89", Style.COLOUR_WHITE,Style.COLOUR_BLACK);
		// <90> catch
		// <9001> throw
		addMapping("<94", Style.COLOUR_WHITE,Style.COLOUR_BLACK);
		addMapping("<95", Style.COLOUR_WHITE,Style.COLOUR_BLACK);
		addMapping("<96>", Style.COLOUR_WHITE,Style.COLOUR_BLACK);
		//addMapping("<98xx", Style.COLOUR_BLACK,Style.COLOUR_BLUE  (BLACK,MAGENTA alternate odd/even)
		//addMapping("<99FFBB>" Style.COLOURs
		addMapping("<9999>", Style.COLOUR_LT_WHITE,Style.COLOUR_BLACK);
		addMapping("<", Style.COLOUR_LT_WHITE,Style.COLOUR_BLACK);
		
		colourStack.add(new Style(Style.COLOUR_WHITE, Style.COLOUR_BLACK, false));
	}

	private void addMapping(String code, int foreground, int background) {
		mapCodeToStyle.put(code, new Style(foreground, background, false));
	}

	private void addMapping(String code, int foreground, int background, boolean bold) {
		mapCodeToStyle.put(code, new Style(foreground, background, bold));
	}
	
	private List colourStack = new ArrayList();
	private List pushedColourStack = null;
	
	public void onCode(String code) {
//		synchronized (this) {
			Style style = null;
			if (code.equals("<>")) {
//				System.out.println("POP  ("+colourStack.size()+") "+code);
				if (colourStack.size() > 0) {
					colourStack.remove(colourStack.size()-1);
//				} else {
//					throw new RuntimeException("BOOM!");
				}
				if (colourStack.size() > 0) {
					style = (Style) colourStack.get(colourStack.size()-1);
				} else {
//					throw new RuntimeException("BOOM ALSO!");
					style = new Style(Style.COLOUR_WHITE, Style.COLOUR_BLACK, false);
				}
			} else {
				style = getStyle(code);
			}
			
			if (style == null) {
				style = new Style(Style.COLOUR_LT_GREEN, Style.COLOUR_MAGENTA, true);
			}
			
			fireOnStyle(style);
//		}
	}

	private Style getStyle(String code) {
		boolean pushStyleOntoStack = true;
		Style style;
		style = (Style) mapCodeToStyle.get(code);
		if (style == null) {
			if (code.equals("<90>")) {
				// the <90><9911>cheesy<><9001> necromancer<>
				pushedColourStack = new ArrayList(colourStack);
				pushStyleOntoStack = false;
				if (colourStack.size() > 0) {
					style = (Style) colourStack.get(colourStack.size()-1);
				} else {
					style = new Style(Style.COLOUR_WHITE, Style.COLOUR_BLACK, false);
				}
			} else if (code.equals("<9001>")) {
				if (pushedColourStack != null) {
					colourStack = new ArrayList(pushedColourStack);
					pushedColourStack = null;
				}
				pushStyleOntoStack = false;
				if (colourStack.size() > 0) {
					style = (Style) colourStack.get(colourStack.size()-1);
				} else {
					style = new Style(Style.COLOUR_WHITE, Style.COLOUR_BLACK, false);
				}
//			} else if (code.startsWith("<97")) {
//				// TODO
			} else if (code.startsWith("<99")) {
				int fg = Style.COLOUR_WHITE;
				int bg = Style.COLOUR_BLACK;
				
				if (code.length() >= 6) {
					fg = Integer.parseInt(code.substring(3,5));
				}
				if (code.length() >= 8) {
					bg = Integer.parseInt(code.substring(5,7));
				}
				style = new Style(convert(fg),convert(bg),false);
				mapCodeToStyle.put(code, style);
			} else {
				String partial = code.substring(0, code.length()-1);
				while (true) {
					style = (Style) mapCodeToStyle.get(partial);
					
					if (style != null) {
						break;
					}
					
					if (partial.length() < 2) {
						break;
					}
					
					partial = partial.substring(0, partial.length()-2);
				}
				
				if (style != null) {
					// cache lookup for next time
					mapCodeToStyle.put(code, style);
				}
			}
		}
		
		if (style == null) {
			style = new Style(Style.COLOUR_GREEN, Style.COLOUR_CYAN, false);
		}
		
		if (code.equals("<00>")) {
			pushStyleOntoStack = false;
		}
		
		if (pushStyleOntoStack) {
			colourStack.add(style);
//			System.out.println("PUSH ("+colourStack.size()+") "+code);
		}
		
		return style;
	}

	private int[] conversion = new int[] {
		Style.COLOUR_BLACK,
		Style.COLOUR_RED,
		Style.COLOUR_GREEN,
		Style.COLOUR_YELLOW,
		Style.COLOUR_BLUE,
		Style.COLOUR_MAGENTA,
		Style.COLOUR_CYAN,
		Style.COLOUR_WHITE,
		Style.COLOUR_LT_BLACK,
		Style.COLOUR_LT_RED,
		Style.COLOUR_LT_GREEN,
		Style.COLOUR_LT_YELLOW,
		Style.COLOUR_LT_BLUE,
		Style.COLOUR_LT_MAGENTA,
		Style.COLOUR_LT_CYAN,
		Style.COLOUR_LT_WHITE
	};
		
	private int convert(int mudColour) {
		return conversion[mudColour];
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
	