package gui3.text;

import gui3.DocumentListener;

import java.awt.Toolkit;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import domain.Style;

public class BetterTextAreaDocument {

//	private List styles;
	
	private static final int SCROLL_BACK_BUFFER_SIZE_IN_KILOBYTES = 500;

	private List lines;

	private BetterTextAreaDocumentLine currentLine;

	private BetterTextAreaDocumentLine lastLine;
	
	private TextAreaDocumentStyle currentStyle;

	private int lineWidth;

	private byte[] buffer;

	private int currentOffset;

	private List documentListeners;

	private TextAreaDocumentPrefix currentPrefix;
	
	public BetterTextAreaDocument() {
		documentListeners = new Vector();
		buffer = new byte[1024*SCROLL_BACK_BUFFER_SIZE_IN_KILOBYTES];
		currentOffset = 0;
		currentPrefix = null;
		currentStyle = new TextAreaDocumentStyle(new Style(Style.COLOUR_WHITE, Style.COLOUR_BLACK, false), currentOffset);
		lines = new ArrayList();
		addNewLine();
		lastLine = currentLine;
	}

	private void addNewLine() {
//		System.out.println("Adding new line with style "+currentStyle.style.getForegroundColour());
		currentLine = new BetterTextAreaDocumentLine(currentOffset, currentStyle, currentPrefix);
		lines.add(0, currentLine);
	}


	public void append(String text) {
		append(text, true);
	}
	
	/* (non-Javadoc)
	 * @see gui3.TextAreaDocument#append(java.lang.String)
	 */
	public void append(String text, boolean isCheckPrefix) {
//		System.out.println("Adding text: "+text);
		
		int linesAdded = 0;
		synchronized (this) {
			for(int i = 0; i<text.length(); i++) {
				byte b = (byte) text.charAt(i);
				if (b == (byte)'\t') {
					int pad = 8 - (currentLine.length % 8);
					for(int j=0; j<pad; j++) {
						linesAdded += append((byte)' ', isCheckPrefix);
					}
				} else {
					linesAdded += append(b, isCheckPrefix);
				}
			}
		}
		if (linesAdded > 0) {
			fireOnNewDocumentLines(linesAdded, lines.size());
		}
	}

	private void fireOnNewDocumentLines(int linesAdded, int linesCount) {
		Iterator it = documentListeners.iterator();
		while(it.hasNext()) {
			DocumentListener documentListener = (DocumentListener) it.next();
			documentListener.onNewDocumentLines(linesAdded, linesCount);
		}
	}


	private void append(byte b) {
		append(b, true);
	}
	
	private int append(byte b, boolean isCheckPrefix) {
		int linesAdded = 0;
		if (b == 7) {
			Toolkit.getDefaultToolkit().beep();
		} else if (b == (char)'\b') {
			currentOffset = (currentOffset-1)%buffer.length;
		} else if (b != 10) {
			if (isCheckPrefix) {
				checkPrefix();
			}
			
			if (lineWidth > 0 && currentLine.length >= lineWidth && b != ' ' && b != '\r') {
				// overflow
				int start = currentOffset;
				int length = 0;
				do {
					start--;
					if (start < 0) {
						start = buffer.length-1;
					}
					length++;
				} while (buffer[start] != ' ');
				start++;
				if (start == buffer.length) {
					start = 0;
				}
				length--;
				if (length < lineWidth) {
//					System.out.println("WRAPPING LINE WAS: "+currentLine.toString(buffer));
					currentLine.length -= length;
//					System.out.println("WRAPPING LINE NOW: "+currentLine.toString(buffer));
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					int i = start;
					for(int j=0; j<length; j++) {
						byteArrayOutputStream.write(buffer[i]);
						i = (i+1)%buffer.length;
					}
					currentOffset = start;
					addNewLine();
					linesAdded++;
					byte[] bytes = byteArrayOutputStream.toByteArray();
					for(i=0; i<bytes.length; i++) {
						append(bytes[i]);
					}
//					System.out.println("WRAPPING NEWLINE IS: "+currentLine.toString(buffer));
				} else {
					addNewLine();
					linesAdded++;
				}
			}
			
			if (isCheckPrefix) {
				checkPrefix();
			}
			
			boolean isBackspaced = (currentLine.offset + currentLine.length)%buffer.length != currentOffset;
			if (b == '\r' && isBackspaced) {
				// backspace case
			} else {
				buffer[currentOffset] = b;
			}
			if (!isBackspaced) {
				currentLine.length++;
			}
			currentOffset++;
			if (currentOffset == buffer.length) {
				currentOffset = 0;
			}
	
			
			if (b == '\r') {
				addNewLine();
				linesAdded++;
			}
			
			if (currentOffset == lastLine.offset) {
				lines.remove(lines.size()-1);
				lastLine = (BetterTextAreaDocumentLine) lines.get(lines.size()-1);
			}
		}
		
		return linesAdded;
	}

	private void checkPrefix() {
		if (currentLine.length == 0) {
			if (currentPrefix != null && currentPrefix.text != null && currentPrefix.text.length() > 0) {
				TextAreaDocumentStyle areaDocumentStyle = currentStyle;
				onStyle(currentPrefix.style);
				append(currentPrefix.text, false);
				onStyle(areaDocumentStyle.style);
				append(" ", false);
			}
		}
	}

	/* (non-Javadoc)
	 * @see gui3.TextAreaDocument#lines(int, int)
	 */
	public ListIterator lines(int lineIndex, int lineCount) {
		if (lineIndex < 0 || lineIndex >= lines.size()) {
			return new ArrayList().listIterator(); // HACK
		}
		return lines.listIterator(lineIndex);
	}

	/* (non-Javadoc)
	 * @see gui3.TextAreaDocument#onStyle(domain.Style)
	 */
	public void onStyle(Style style) {
		if (style.getForegroundColour() == 0 && style.getBackgroundColour() == 0) {
			// NOTE fix for black on black when grey on black is intended 
			style = new Style(Style.COLOUR_LT_BLACK, Style.COLOUR_BLACK, false);
		}
		
		synchronized (this) {
//			System.out.println("Adding style "+style.getForegroundColour());
			TextAreaDocumentStyle nextStyle = new TextAreaDocumentStyle(style, currentOffset);
			currentStyle.next = nextStyle;
			currentStyle = nextStyle;
			
			if (currentLine.offset == currentStyle.offset) {
				currentLine.firstStyle = currentStyle;
			}
		}
	}

	/* (non-Javadoc)
	 * @see gui3.TextAreaDocument#setLineWidth(int)
	 */
	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}
	
	public int getLineWidth() {
		return lineWidth;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public int getLineCount() {
		return lines.size();
	}
	
	public void addDocumentListener(DocumentListener documentListener) {
		documentListeners.add(documentListener);
	}

	public void setPrefix(TextAreaDocumentPrefix areaDocumentPrefix) {
		if (currentPrefix != areaDocumentPrefix) {
			currentPrefix = areaDocumentPrefix;
			addNewLine();
		} else {
			currentPrefix = areaDocumentPrefix;
		}
	}

}
