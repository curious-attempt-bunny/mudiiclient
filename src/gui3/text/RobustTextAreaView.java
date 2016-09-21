package gui3.text;

import gui3.ColourHelper;
import gui3.DocumentListener;
import gui3.ViewListener;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.JPanel;

import domain.Configuration;
import domain.Style;
import io.sensor.PlanSensor;

public class RobustTextAreaView extends JPanel implements MouseListener, MouseMotionListener, DocumentListener, ClipboardOwner, TextView, MouseWheelListener {

	private final int MARGIN = 5;
	
	private boolean isFontChanged;

	private int fontWidth;

	private int fontHeight;
	
	private int lineIndex;

	private int fontDescent;

	private Font font;

	private final ColourHelper colourHelper;

	private final BetterTextAreaDocument document;
	
	private Configuration configuration;

	private boolean isScrollback;
	
	private Point selectStart;

	private Point selectEnd;

	private List viewListeners;
	private ScrollbackController scrollbackController;

	private PlanSensor plan;

	public RobustTextAreaView(ColourHelper colourHelper, BetterTextAreaDocument document, ScrollbackController scrollbackController) {
		this.colourHelper = colourHelper;
		this.document = document;
		this.scrollbackController = scrollbackController;

		viewListeners = new Vector();
		
		isFontChanged = true;
		
		addMouseListener(this);
		addMouseMotionListener(this);
		document.addDocumentListener(this);
		addMouseWheelListener(this);
	}
	
	// TODO add to interface
	public void setFont(Font font) {
		this.font = font;
		isFontChanged = true;
		repaint();
	}
	
	// TODO add to interface
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	public Dimension getMinimumSize() {
		if (isScrollback) {
			return new Dimension(fontWidth, fontHeight*1);
		} else {
			return new Dimension(fontWidth, fontHeight*5);
		}
	}
	
	public void paint(Graphics gc) {
		((Graphics2D)gc).setRenderingHint(
			RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		checkFont(gc);

		checkLineWidth();
		
		paintBackground(gc);
		
		int y = getFirstLineOffset();
		
		synchronized (document) {
			ListIterator lineIterator = getLinesIterator();
			while (lineIterator.hasNext()) {
				if (!isLineVisible(y)) {
					break;
				}
				
				BetterTextAreaDocumentLine line = (BetterTextAreaDocumentLine) lineIterator.next();
				paintLine(gc, line, y);
				
				y -= fontHeight;
			}
		}
		
		paintSelection(gc);

		paintPlan(gc);
	}

	public void paintPlan(Graphics gc) {
		if (plan != null) {
			List labels = plan.getLabels();

			if (labels.size() > 0) {
				int top = 50;
				int right = (int) (getSize().getWidth() - 2*fontWidth);
				int left = (int) (getSize().getWidth() - ("abcdef12".length() * fontWidth) - 3*2);
				int bottom = top + (fontHeight * labels.size()) + 3*2;
				int width = right - left;
				int height = bottom - top;

				gc.setColor(new Color(0,0,0));
				gc.fillRect(left, top, width, height);
				gc.setColor(new Color(127, 127, 127));
				gc.drawRect(left, top, width, height);
				gc.setColor(new Color(255,255,255));

				Iterator iterator = labels.iterator();
				while(iterator.hasNext()) {
					String label = (String) iterator.next();
					gc.drawBytes(label.getBytes(), 0, label.length(), left+3, top+3+fontHeight-fontDescent);
					top += fontHeight;
				}
			}
		}
	}

	private void paintSelection(Graphics gc) {
		int y;
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
				int x2 = document.getLineWidth();
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
	
	private void checkFont(Graphics gc) {
		if (isFontChanged) {
			// TODO move this logic to the FontManager
			FontMetrics fontMetrics = gc.getFontMetrics(font);
			Rectangle2D stringBounds = fontMetrics.getStringBounds("This is a test", gc);
			fontHeight = (int) Math.ceil(stringBounds.getHeight()); //fontMetrics.getHeight();
			fontWidth = (int) stringBounds.getWidth() / "This is a test".length(); // fontMetrics.charWidth('A');
			fontDescent = fontMetrics.getDescent();
			isFontChanged = false;			
		}
		
		gc.setFont(font);
	}

	private void paintLine(Graphics gc, BetterTextAreaDocumentLine line, int y) {
		byte[] buffer = document.getBuffer();
		TextAreaDocumentStyle currentStyle = line.firstStyle;
		int offset = line.offset;
		int remaining = line.length;
		int x = MARGIN;
		
		while(remaining > 0) {
			currentStyle = getStyleToDraw(line, offset, currentStyle);
			int length = getLengthToDraw(line, offset, remaining, currentStyle);
			
			gc.setColor(colourHelper.get(currentStyle.style.getBackgroundColour()));
			gc.fillRect(x, y-fontHeight+fontDescent, fontWidth*length, fontHeight);
			gc.setColor(colourHelper.get(currentStyle.style.getForegroundColour()));
			gc.drawBytes(buffer, offset, length, x, y);
			
			offset = (offset+length)%buffer.length;
			remaining -= length;
			x += fontWidth*length;
		}
	}

	private TextAreaDocumentStyle getStyleToDraw(BetterTextAreaDocumentLine line, int offset, TextAreaDocumentStyle currentStyle) {
		TextAreaDocumentStyle style = currentStyle;
		
		while (style.next != null && style.next.offset == offset) {
			style = style.next;
		}

		return style;
	}

	private int getLengthToDraw(BetterTextAreaDocumentLine line, int offset, int remaining, TextAreaDocumentStyle currentStyle) {
		int trimmedLength;
		
		byte[] buffer = document.getBuffer();
		if (offset + remaining >= buffer.length) {
			// wrapping case
			trimmedLength = buffer.length - offset;
		} else {
			trimmedLength = remaining;
		}
		
		if (currentStyle.next != null) {
			if (currentStyle.next.offset >= offset) {
				if (currentStyle.next.offset < offset+trimmedLength) {
					trimmedLength = currentStyle.next.offset - offset;
				}
			}
		}
		
		return trimmedLength;
	}

	private int getFirstLineOffset() {
		Dimension size = getSize();
		int y = size.height - fontDescent;
		return y;
	}

	private void paintBackground(Graphics gc) {
		Dimension size = getSize();
		gc.setColor(colourHelper.get(Style.COLOUR_BLACK));
		gc.fillRect(0, 0, (int) size.getWidth(), (int) size.getHeight());
	}

	private void checkLineWidth() {
		Dimension size = getSize();
		
		int lineWidth = (int) ((size.getWidth()-2*MARGIN)/fontWidth);
		if (configuration.getInt(Configuration.KEY_MAX_WIDTH_80, Configuration.DEFAULT_MAX_WIDTH_80) == 1) {
			lineWidth = 80;
		}
		
		document.setLineWidth(lineWidth);
	}

	private boolean isLineVisible(int y) {
		return y > -fontHeight;
	}

	private ListIterator getLinesIterator() {
		int height = (int)getSize().getHeight();
		return document.lines(lineIndex, 1+height/fontHeight);
	}

	public void setScrollback(boolean isScrollback) {
		this.isScrollback = isScrollback;
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

	public void setLineIndex(int i) {
		lineIndex = i;
		fireOnViewLineChange(lineIndex);
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		// we want to route the mouse wheel movement to the correct view (plus enable and disable scrollback)
		scrollbackController.move(e.getWheelRotation());
	}

	public void onMove(int wheelRotation) {
		if (configuration.getInt(Configuration.KEY_INVERT_MOUSE_WHEEL_SCROLLING, 0) == 1) {
			lineIndex += wheelRotation;
		} else {
			lineIndex -= wheelRotation;
		}

		if (lineIndex >= document.getLineCount()) {
			lineIndex = document.getLineCount()-1;
		} else if (lineIndex <= 0) {
			lineIndex = 0;
		}
		fireOnViewLineChange(lineIndex);
		repaint();
	}

	public void addViewListener(ViewListener viewListener) {
		viewListeners.add(viewListener);
	}

	private Point getTextRelativePoint(Point pixelPoint, boolean isRoundUp) {
		int y = Math.max(0, Math.min(document.getLineCount()-1, (lineIndex + (getHeight()-pixelPoint.y)/fontHeight)));
		double x = (pixelPoint.x-MARGIN)/(double)fontWidth;
		if (isRoundUp) {
			x = Math.ceil(x);
		} else {
			x = Math.floor(x);
		}
		x = Math.min(document.getLineWidth(), Math.max(0,x));
		Point textPoint = new Point((int)x, y);
//		System.out.println("point is: "+x+", "+y+" (linecount="+getDocument().getLineCount()+")");
		return textPoint;
	}
	
	public void mouseClicked(MouseEvent arg0) {
		Point clickPoint = getTextRelativePoint(arg0.getPoint(), false);
		Iterator it = document.lines(clickPoint.y, -1);
		if (it.hasNext()) {
			BetterTextAreaDocumentLine line = (BetterTextAreaDocumentLine) it.next();
			String text = new String(document.getBuffer(), line.offset, line.length);
			int left = clickPoint.x;
			int right = clickPoint.x;
			while(left >= text.length()) {
				left--;
				right--;
			}
			while(left > 0 && text.charAt(left-1) != ' ' && text.charAt(left-1) != '"') {
				left--;
			}
			while(right < text.length()-1 && text.charAt(right) != ' ' && text.charAt(right) != '"') {
				right++;
			}
			if (right > left && (text.charAt(right-1) == '.' || text.charAt(right-1) == ',' || text.charAt(right-1) == '!' || text.charAt(right-1) == '?')) {
				// don't include punctuation on the right hand side (makes copying URLs work better)
				right--;
			}
			selectStart = new Point(left, clickPoint.y);
			selectEnd = new Point(right, clickPoint.y);
			repaint();
			copySelection();
		}
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
			selectEnd = selectStart;
			repaint();
		} else if (arg0.getButton() == MouseEvent.BUTTON2) {
			pasteSelection();
		}
	}

	private void pasteSelection() {
		// TODO Auto-generated method stub
		
	}

	private void copySelection() {
		synchronized (document) {
			Point start = selectStart;
			Point end = selectEnd;
			if (isSelectInverse()) {
				start = selectEnd;
				end = selectStart;
			}
			
			StringBuffer buf = new StringBuffer();
			Iterator it = document.lines(start.y, end.y-start.y+1);
			int y = start.y;
			while(y <= end.y && it.hasNext()) {
				BetterTextAreaDocumentLine line = (BetterTextAreaDocumentLine) it.next();
				int x1 = 0;
				int x2 = document.getLineWidth();
				if (y == start.y) {
					x2 = start.x;
				} 
				if (y == end.y) {
					x1 = end.x;
				}
				x1 = Math.min(x1, line.length);
				x2 = Math.min(x2, line.length);
				
				int offset = line.offset+x1;
				int length = x2-x1;
				
				if (offset+length >= document.getBuffer().length) {
					int trimmedLength = document.getBuffer().length-offset;
					buf.insert(0, new String(document.getBuffer(), offset, trimmedLength));
					buf.insert(0, new String(document.getBuffer(), 0, length - trimmedLength));
				} else {
					buf.insert(0, new String(document.getBuffer(), offset, length));
				}
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

	public void setPlan(PlanSensor plan) {
		this.plan = plan;
	}
}
