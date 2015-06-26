package domain;


public class Style {
	public static final int COLOUR_BLACK = 0;
	public static final int COLOUR_RED = 1;
	public static final int COLOUR_GREEN = 2;
	public static final int COLOUR_YELLOW = 3;
	public static final int COLOUR_BLUE = 4;
	public static final int COLOUR_MAGENTA = 5;
	public static final int COLOUR_CYAN = 6;
	public static final int COLOUR_WHITE = 7;
	public static final int COLOUR_LT_BLACK = 8; 
	public static final int COLOUR_LT_RED = 9; 
	public static final int COLOUR_LT_GREEN = 10;
	public static final int COLOUR_LT_YELLOW = 11;
	public static final int COLOUR_LT_BLUE = 12;
	public static final int COLOUR_LT_MAGENTA = 13;
	public static final int COLOUR_LT_CYAN = 14; 
	public static final int COLOUR_LT_WHITE = 15;
	
	//private final int[] foregroundRGB;
	//private final int[] backgroundRGB;
	private int foregroundColour;
	private int backgroundColour;
	private final boolean bold;

	//public Style(int[] foregroundRGB, int[] backgroundRGB, boolean bold) {
	public Style(int foregroundColour, int backgroundColour, boolean bold) {
		this.foregroundColour = foregroundColour;
		this.backgroundColour = backgroundColour;
		this.bold = bold;
	}
	
	public boolean isBold() {
		return bold;
	}

	public int getBackgroundColour() {
		return backgroundColour;
	}

	public int getForegroundColour() {
		return foregroundColour;
	}

//	public int[] getBackgroundRGB() {
//		return backgroundRGB;
//	}
//
//
//	public int[] getForegroundRGB() {
//		return foregroundRGB;
//	}
}
