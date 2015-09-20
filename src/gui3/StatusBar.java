package gui3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import domain.State;

public class StatusBar extends JPanel {
	public static final int INDEX_STAMINA = 0;

	private Dimension minimumSize;
	
	private StatusBarItem[] items;

	private boolean isFontChanged;

	private int fontHeight;

	private int fontWidth;

	private int fontAscent;

	private Map mapKeyToIndex;
	
	private Font font;
	
	private final int MARGIN = 5;
	
	class StatusBarItem {
		String text;
		int offset;
		int length;
		Color color;
		private final String smallText;
		private final int smallOffset;
		private final int smallLength;
		public boolean emphasize;

		StatusBarItem(String text, int offset, int length, Color color, String smallText, int smallOffset, int smallLength) {
			this.text = text;
			this.offset = offset;
			this.length = length;
			this.color = color;
			this.smallText = smallText;
			this.smallOffset = smallOffset;
			this.smallLength = smallLength;
		}
		
		String getText(boolean isSmallText) {
			if (isSmallText) {
				if (smallText != null) {
					return smallText;
				}
				
				if (smallLength > 0) {
					return text.substring(0,Math.min(text.length(), smallLength));
				}
			}
			
			return text;
		}
		
		int getOffset(boolean isSmallText) {
			return (isSmallText ? smallOffset : offset);
		}

		public boolean isDrawable(boolean isSmallText) {
			if (text == null) {
				return false;
			}
			
			if (isSmallText && smallLength == -1) {
				return false;
			}
			
			return true;
		}
	}
	
	public StatusBar() {
		minimumSize = new Dimension(16,16);
		items = new StatusBarItem[] {
			new StatusBarItem("Sta:   /    Dex:   /    Str:   /    Mag:    Pts:                                         ", 0, 0, new Color(0x00,0xa8,0xa8),
			//  																	   Blind Crippled Deaf Dumb overcast sw.nw.ne.ne.ne.n
			//				   01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789
			//				             1         2         3         4         5         6         7         8		 9		   0 	   	
							  "Sta:   /    Dex:    Str:    Mag:    Pts:                                         ", 0, 0),
								//  							               BlCrDeDu o sw.nw.ne.ne.ne.n
							// 01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789
							//           1         2         3         4         5         6         7         8		 9		   0 	   	
			new StatusBarItem(null,4,3,null,null,4,3),
			new StatusBarItem(null,8,0,null,null,8,0),
			new StatusBarItem(null,16,3,null,null,16,3),
			new StatusBarItem(null,20,0,null,null,-1,-1),
			new StatusBarItem(null,28,3,null,null,24,3),
			new StatusBarItem(null,32,0,null,null,-1,-1),
			new StatusBarItem(null,40,3,null,null,32,3),
			new StatusBarItem(null,48,0,null,null,40,0),
			new StatusBarItem(null,56,0,null,null,48,2),
			new StatusBarItem(null,62,0,null,null,50,2),
			new StatusBarItem(null,71,0,null,null,52,2),
			new StatusBarItem(null,76,0,null,null,54,2),
			new StatusBarItem(null,81,0,null,null,57,1),
			new StatusBarItem(null,90,0,null,null,59,0),
			new StatusBarItem(null,107,0,null,null,76,0)
		};
		isFontChanged = true;
		
		mapKeyToIndex = new HashMap();
		mapKeyToIndex.put(State.KEY_STAMINA, new Integer(1));
		mapKeyToIndex.put(State.KEY_STAMINA_MAX, new Integer(2));
		mapKeyToIndex.put(State.KEY_DEXTERITY_EFFECTIVE, new Integer(3));
		mapKeyToIndex.put(State.KEY_DEXTERITY, new Integer(4));
		mapKeyToIndex.put(State.KEY_STRENGTH_EFFECTIVE, new Integer(5));
		mapKeyToIndex.put(State.KEY_STRENGTH, new Integer(6));
		mapKeyToIndex.put(State.KEY_MAGIC, new Integer(7));
		mapKeyToIndex.put(State.KEY_POINTS, new Integer(8));
		mapKeyToIndex.put(State.KEY_BLIND, new Integer(9));
		mapKeyToIndex.put(State.KEY_CRIPPLED, new Integer(10));
		mapKeyToIndex.put(State.KEY_DEAF, new Integer(11));
		mapKeyToIndex.put(State.KEY_DUMB, new Integer(12));
		mapKeyToIndex.put(State.KEY_WEATHER, new Integer(13));
		mapKeyToIndex.put(State.KEY_CHART_DIRS, new Integer(14));
		mapKeyToIndex.put(State.KEY_RESET_TIME, new Integer(15));
	}
	
	public void setFont(Font font) {
//		super.setFont(font);
		this.font = font;
		isFontChanged = true;
		repaint();
	}
	
	public Dimension getPreferredSize() {
		return minimumSize;
	}
	
	public void paint(Graphics g) {
		g.setFont(font);
		if (isFontChanged) {
			FontMetrics fontMetrics = g.getFontMetrics();
			fontHeight =  fontMetrics.getHeight(); // fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent() + fontMetrics.getLeading() +
			fontWidth = fontMetrics.charWidth('A'); //fontMetrics.getMaxAdvance();
			fontAscent = fontMetrics.getAscent();
			minimumSize.setSize(10, MARGIN*2 + fontHeight);
			getParent().doLayout();
		}
		
		g.setColor(new Color(0,0,0));
		Dimension size = getSize();
		g.fillRect(0, 0, (int)size.getWidth(), (int)size.getHeight());

		g.setColor(new Color(255,255,255));
		g.drawLine(0, (int)size.getHeight()-1, (int)size.getWidth()-1, (int)size.getHeight()-1);
		
		boolean isSmallText = (size.getWidth()/fontWidth < 110);
		
		for(int i=0; i<items.length; i++) {
			if (items[i].isDrawable(isSmallText)) {
				String text = items[i].getText(isSmallText);
				int offset = items[i].getOffset(isSmallText);
				if (i == items.length - 1) {
					// right-align the last item
					offset = Math.max(offset, (int)((size.getWidth() - 2*MARGIN)/fontWidth) - text.length());
				}
				if (items[i].emphasize) {
					g.setColor(items[i].color);
					g.fillRect(MARGIN + offset*fontWidth, MARGIN, text.length()*fontWidth, fontHeight);
					g.setColor(new Color(0, 0, 0));
				} else {
					g.setColor(items[i].color);
				}
				g.drawString(text, MARGIN + offset*fontWidth, MARGIN + fontAscent);
			}
		}
	}
	
	public void setItem(String key, Color color, String text, boolean emphasize) {
		Integer index = (Integer)mapKeyToIndex.get(key);
		if (index != null) {
			items[index.intValue()].color = color;
			items[index.intValue()].emphasize = emphasize;
			while(text.length() < items[index.intValue()].length) {
				text = " " + text;
			}
			items[index.intValue()].text = text;
		}
	}
	
	
}
