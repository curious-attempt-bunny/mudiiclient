package gui3.text;

import gui3.ColourHelper;
import gui3.DocumentListener;
import gui3.ViewListener;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import domain.Configuration;
import domain.Style;

public class BetterTextAreaView extends JPanel implements MouseListener, MouseMotionListener, DocumentListener, ClipboardOwner, TextView {

	private BetterTextAreaDocument document;

	private ColourHelper colourHelper;
	
	private boolean isFontChanged;

	private int fontWidth;

	private int fontHeight;
	
	private int lineIndex;

	private int fontDescent;

	private Font font;

	private final int MARGIN = 5;

	private Image image;

	private boolean isScrollback;

	private List viewListeners;
	
	private Point selectStart;

	private Point selectEnd;

	private Configuration configuration;
	
	public BetterTextAreaView(ColourHelper colourHelper, BetterTextAreaDocument document) {
		viewListeners = new Vector();
		
		this.colourHelper = colourHelper;
		
		lineIndex = 0;
		
		this.document = document;
		
		isFontChanged = true;
		
		try {
			image = new ImageIcon(getClass().getResource("/mudiicouk600.png")).getImage();
		} catch (Exception e) {
			
		}
		
		addMouseListener(this);
		addMouseMotionListener(this);
		getDocument().addDocumentListener(this);
	}

	public void setFont(Font font) {
		this.font = font;
		isFontChanged = true;
		repaint();
	}

	public void paint(Graphics gc) {
		((Graphics2D)gc).setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		Dimension size = getSize();
		if (isFontChanged) {
			FontMetrics fontMetrics = gc.getFontMetrics(font);
			Rectangle2D stringBounds = fontMetrics.getStringBounds("This is a test", gc);
			fontHeight = (int) Math.ceil(stringBounds.getHeight()); //fontMetrics.getHeight();
			fontWidth = (int) stringBounds.getWidth() / "This is a test".length(); // fontMetrics.charWidth('A');
			fontDescent = fontMetrics.getDescent();
			isFontChanged = false;			
		}

		int lineWidth = (int) ((size.getWidth()-2*MARGIN)/fontWidth);
		if (configuration.getInt(Configuration.KEY_MAX_WIDTH_80, Configuration.DEFAULT_MAX_WIDTH_80) == 1) {
			lineWidth = 80;
		}
		getDocument().setLineWidth(lineWidth);

		gc.setColor(colourHelper.get(Style.COLOUR_BLACK));
		gc.fillRect(0, 0, (int)size.getWidth(), (int)size.getHeight());
		
		int y = size.height - fontDescent;
		
		gc.setFont(font);
		
		synchronized (getDocument()) {
//			System.out.println("PAINT");
			byte[] buffer = ((BetterTextAreaDocument)getDocument()).getBuffer();
			ListIterator lineIterator = getDocument().lines(lineIndex, 1+(int)(size.getHeight()/fontHeight));
			while (lineIterator.hasNext() && y > -fontHeight) {
				BetterTextAreaDocumentLine line = (BetterTextAreaDocumentLine) lineIterator
						.next();
				TextAreaDocumentStyle style = line.firstStyle;
				TextAreaDocumentStyle next = line.firstStyle.next;
//				System.out.println("LINE "+line.offset+"+"+line.length);
				int x = MARGIN;
				int offset = line.offset;
				int length;
				int remaining = line.length;
				boolean isWrappedStyle = false;
				if (next != null && next.offset < offset) {
					isWrappedStyle = true;
				}
				while(remaining > 0) {
					length = remaining;
					if (next != null) {
						if (isWrappedStyle) {
							if ((offset+length)%buffer.length > next.offset) {
								length -= (offset+length)%buffer.length - next.offset;
							}
						} else {
							if (offset+length > next.offset) {
								length = next.offset - offset;
							}
						}
					}
					
					gc.setColor(colourHelper.get(style.style.getBackgroundColour()));
					gc.fillRect(x, y-fontHeight+fontDescent, fontWidth*length, fontHeight);
					gc.setColor(colourHelper.get(style.style.getForegroundColour()));
//					System.out.println("DRAW "+offset+"+"+length+": "+new String(buffer, offset, length));
					if (offset+length >= buffer.length) {
						// draw two chunks if buffer wraps
						gc.drawBytes(buffer, offset, buffer.length-offset, x, y);
						gc.drawBytes(buffer, 0, length-(buffer.length-offset), x + ((buffer.length-offset) * fontWidth), y);
//						gc.drawLine(x, y, x+length*fontWidth, y);
					} else {
						gc.drawBytes(buffer, offset, length, x, y); // crashed here with negative array size
					}
					
					x += length * fontWidth;
					offset = (offset + length)%buffer.length;
					remaining -= length;
					
					if (next != null && next.offset == offset) {
						style = next;
						next = style.next;
						if (next != null && next.offset < offset) {
							isWrappedStyle = true;
						}
					}
				}
				y -= fontHeight;
			}
		}
		
		if (image != null && y > 0 && image.getWidth(null) != -1 && image.getHeight(null) != -1) {
			gc.drawImage(image, (size.width-image.getWidth(null))/2, y-image.getHeight(null), null);
		}
		
		if (selectStart != null && selectEnd != null) {
			Point start = selectStart;
			Point end = selectEnd;
			if (isSelectInverse()) {
				start = selectEnd;
				end = selectStart;
			}
			
			gc.setColor(new Color(0,0,0));
			gc.setXORMode(new Color(255,255,255));
			
			int offset = getHeight();
			for(y=start.y; y<=end.y; y++) {
				int x1 = 0;
				int x2 = getDocument().getLineWidth();
				if (y == start.y) {
					x2 = start.x;
				} 
				if (y == end.y) {
					x1 = end.x;
				}
				gc.fillRect(MARGIN+x1*fontWidth, offset-(1+y-lineIndex)*fontHeight, (x2-x1)*fontWidth, fontHeight);
			}
		}
	}

	private boolean isSelectInverse() {
		return selectStart.y > selectEnd.y || (selectStart.y == selectEnd.y && selectStart.x <= selectEnd.x);
	}

	public BetterTextAreaDocument getDocument() {
		return document;
	}

	public void setDocument(BetterTextAreaDocument document) {
		this.document = document;
	}

	public void onPageUp() {
		lineIndex += getPageSize();
		if (lineIndex >= document.getLineCount()) {
			lineIndex = document.getLineCount()-1;
		}
		fireOnViewLineChange(lineIndex);
		repaint();
	}

	private void fireOnViewLineChange(int lineIndex) {
		Iterator it = viewListeners.iterator();
		while(it.hasNext()) {
			ViewListener viewListener = (gui3.ViewListener) it.next();
			
			viewListener.onViewLineChange(lineIndex);
		}
	}

	public void onPageDown() {
		lineIndex -= getPageSize();
		fireOnViewLineChange(lineIndex);
		repaint();
	}

	public int getPageSize() {
		if (fontHeight == 0) {
			return 15;
		} else {
			return (getSize().height / fontHeight) - 1;
		}
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public void setColourHelper(ColourHelper colourHelper) {
		this.colourHelper = colourHelper;
	}

	public void setScrollback(boolean isScrollback) {
		this.isScrollback = isScrollback;
	}

	public Dimension getMinimumSize() {
		if (isScrollback) {
			return new Dimension(fontWidth, fontHeight*1);
		} else {
			return new Dimension(fontWidth, fontHeight*5);
		}
	}

	public void setLineIndex(int i) {
		lineIndex = i;
		fireOnViewLineChange(lineIndex);
	}

	public void addViewListener(ViewListener viewListener) {
		viewListeners.add(viewListener);
	}

	private Point getTextRelativePoint(Point pixelPoint, boolean isRoundUp) {
		int y = Math.max(0, Math.min(getDocument().getLineCount()-1, (lineIndex + (getHeight()-pixelPoint.y)/fontHeight)));
		double x = (pixelPoint.x-MARGIN)/(double)fontWidth;
		if (isRoundUp) {
			x = Math.ceil(x);
		} else {
			x = Math.floor(x);
		}
		x = Math.min(getDocument().getLineWidth(), Math.max(0,x));
		Point textPoint = new Point((int)x, y);
//		System.out.println("point is: "+x+", "+y+" (linecount="+getDocument().getLineCount()+")");
		return textPoint;
	}
	
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		if (arg0.getButton() == MouseEvent.BUTTON1 && fontHeight != 0) {
//			System.out.print("pressed: ");	
			selectStart = getTextRelativePoint(arg0.getPoint(), false);
			selectEnd = selectStart; // new Point(selectStart.x+1, selectStart.y);
			repaint();
//			copySelection();
		} else if (arg0.getButton() == MouseEvent.BUTTON2) {
			pasteSelection();
		}
	}

	private void pasteSelection() {
		// TODO Auto-generated method stub
		
	}

	private void copySelection() {
		synchronized (getDocument()) {
			Point start = selectStart;
			Point end = selectEnd;
			if (isSelectInverse()) {
				start = selectEnd;
				end = selectStart;
			}
			
			StringBuffer buf = new StringBuffer();
			Iterator it = getDocument().lines(start.y, end.y-start.y+1);
			int y = start.y;
			while(y <= end.y && it.hasNext()) {
				BetterTextAreaDocumentLine line = (BetterTextAreaDocumentLine) it.next();
				int x1 = 0;
				int x2 = getDocument().getLineWidth();
				if (y == start.y) {
					x2 = start.x;
				} 
				if (y == end.y) {
					x1 = end.x;
				}
				x1 = Math.min(x1, line.length);
				x2 = Math.min(x2, line.length);
				buf.insert(0, new String(getDocument().getBuffer(), line.offset+x1, x2-x1));
				y++;
			}
			
//			System.out.println("SELECTION:");
//			System.out.println(buf.toString());
			StringSelection stringSelection = new StringSelection( buf.toString() );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, this );			
		}
	}

	public void mouseReleased(MouseEvent arg0) {
//		System.out.print("released: ");
//		selectEnd = getTextRelativePoint(arg0.getPoint());
	}

	public void mouseDragged(MouseEvent arg0) {
//		System.out.print("dragged: ");
		Point newPoint = getTextRelativePoint(arg0.getPoint(), isSelectInverse());
		if (newPoint.equals(selectStart)) {
			newPoint = new Point(selectStart.x+1, selectStart.y);
		}
		if (!newPoint.equals(selectEnd)) {
			selectEnd = newPoint;
			repaint();
			copySelection();
		}
	}

	public void mouseMoved(MouseEvent arg0) {
//		System.out.print("moved: ");
//		selectEnd = getTextRelativePoint(arg0.getPoint());
	}

	public void onNewDocumentLines(int linesAdded, int linesCount) {
		if (selectStart != null) {
			selectStart = new Point(selectStart.x, Math.min(linesCount-1, selectStart.y + linesAdded));
		}
		if (selectEnd != null) {
			selectEnd = new Point(selectEnd.x, Math.min(linesCount-1, selectEnd.y + linesAdded));
		}
		repaint();
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub
		
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
