package gui3.frontend;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Scrollable;

public class CharacterPanel extends JPanel implements Scrollable {

	private Image image;
	private boolean isSelected;
	private final CharacterData data;
	private static int[] levelScores = new int[] {
		0, 200, 400, 800, 1600, 3200, 6400, 12800, 25600, 51200, 102400, 204800, Integer.MAX_VALUE
	};
	private static String[][][][] prefix = new String[2][2][2][12];
	private static String[][][][] postfix = new String[2][2][2][12];
	static {
		setPostfixes(true, false, false, null, "protector", "yeoman", "warrior", "swordsman", "hero", "superhero", "champion", "guardian", "legend", null, null);
		setPostfixes(true, true, false, null, "seer", "soothsayer", "cabalist", "magician", "enchanter", "spellbinder", "sorcerer", "necromancer", "warlock", "mage", "wizard");
		prefix[1][1][0][10] = "Sir";
		prefix[0][1][0][10] = "Lady";
		prefix[1][1][1][6] = "Brother";
		prefix[0][1][1][6] = "Sister";
		setPostfixes(false, false, false, null, "protector", "yeowoman", "warrior", "swordswoman", "heroine", "superheroine", "championne", "guardienne", "legend", null, null);
		setPostfixes(false, true, false, null, "seeress", "soothsayer", "cabalist", "magicienne", "enchantress", "spellbindress", "sorcereress", "necromancess", "warlock", "mage", "witch");
		setPostfixes(true, false, true, "discoverer", "pathfinder", "voyager", "wayfarer", "scout", "rover", "pioneer", "explorer", "ranger", "minstrel", null, null);
		setPostfixes(true, true, true, null, "neophyte", "pilgrim", "acolyte", "friar", "cleric", null, "priest", "prelate", "patriarch", null, null);
		setPostfixes(false, false, true, "discoverer", "pathfinder", "voyager", "wayfarer", "scout", "rover", "pioneer", "explorer", "ranger", "minstrel", null, null);
		setPostfixes(false, true, true, null, "neophyte", "pilgrim", "acolyte", "friar", "cleric", null, "priestess", "prelate", "matriarch", null, null);
	}

	public CharacterPanel(CharacterData data) {
		this.data = data;
		String iconFilename = "/head.png";
		if (getClass().getResource("/"+data.name.toLowerCase()+".png") != null) {
			iconFilename = "/"+data.name.toLowerCase()+".png";
		}
		image = new ImageIcon(getClass().getResource(iconFilename)).getImage();
	}

	private static void setPostfixes(boolean isMale, boolean isMagical, boolean isProtected, String string0, String string, String string2, String string3, String string4, String string5, String string6, String string7, String string8, String string9, String string10, String string11) {
		String[] postfixes = postfix[(isMale ? 1 : 0)][(isMagical ? 1 : 0)][(isProtected ? 1 : 0)];
		setArray(postfixes, string0, string, string2, string3, string4, string5, string6, string7, string8, string9, string10, string11);
	}

	private static void setArray(String[] array, String string0, String string, String string2, String string3, String string4, String string5, String string6, String string7, String string8, String string9, String string10, String string11) {
		array[0] = string0;
		array[1] = string;
		array[2] = string2;
		array[3] = string3;
		array[4] = string4;
		array[5] = string5;
		array[6] = string6;
		array[7] = string7;
		array[8] = string8;
		array[9] = string9;
		array[10] = string10;
		array[11] = string11;
	}

	public Dimension getPreferredSize() {
		return new Dimension(2*128+5*2,128+5*2);
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		int fontHeight = g.getFontMetrics(g.getFont()).getHeight();
		int fontWidth = g.getFontMetrics(g.getFont()).charWidth('A');
		
		int leftOffset = 1*128+10;
		int imageSize = 128;
		
		if (leftOffset+45*fontWidth > getWidth()) {
			leftOffset = 1*64+10;
			imageSize = 64;
		}
		
		g.setColor((isSelected ? new Color(0,0,0xae) : new Color(0,0,0x5e)));
		g.fillRect(0, 0, getWidth(), getHeight());
		g.drawImage(image, 5, 5, 5+imageSize, 5+imageSize, 0, 0, image.getWidth(null), image.getHeight(null), null);
		
		g.setColor(new Color(0xff,0xbb, 0x3f));
		g.drawString(getTitle(), leftOffset, 5+fontHeight);
		
		String stats1 = "Score: "+data.score+"   Gender: "+(data.isMale ? "male" : "female")+(data.isProtected ? "   Protected: yes" : "");
		String stats2 = "Stamina: "+data.stamina+"/"+data.staminaMax+(data.isMagical ? "   Magic: "+data.magic+"/"+data.staminaMax : "")+"   Dex "+data.dex+"   Str "+data.str;
		g.drawString(stats1, leftOffset, 5+fontHeight*2);
		g.drawString(stats2, leftOffset, 5+fontHeight*3);
		
		int x1 = leftOffset+45*fontWidth;
		int x2 = getWidth() - 10;
		int y1 = 10;
		int y2 = getHeight() - 10;
		
		if (x2-x1 < 96) {
			if (imageSize == 128) {
				x1 = leftOffset;
				y1 = 5+fontHeight*4;
			} else {
				x1 = 5;
				y1 = 5*2+imageSize;
			}
		}
		
		{
			g.setColor(new Color(0,0,0));
			g.fillRect(x1-1, y1-1, x2-x1+2, y2-y1+2);
			g.setColor(new Color(0,0,0x4e));
			g.drawRect(x1-2, y1-2, x2-x1+3, y2-y1+3);
			
			boolean isDrawn = false;
			List pts = data.getScoreHistory();
			if (pts.size() > 1) {
				int min = Integer.MAX_VALUE;
				int max = Integer.MIN_VALUE;
				for (Iterator it = pts.iterator(); it.hasNext();) {
					int p = ((Integer)it.next()).intValue();
					if (p < min) {
						min = p;
					} 
					if (p > max) {
						max = p;
					}
				}
				min = 0;
				
				if (max - min != 0) {
					isDrawn = true;
					double xStep = (x2-x1) / (double)(pts.size()-1);
					double yStep = (y2-y1) / (double)(max - min);
					
					int yLine = 0;
					g.setColor(new Color(0x1e, 0x1e, 0x1e));
					for(int i=levelScores.length-1; i >= 0; i--) {
						int y = (int) (y2 - yStep*(levelScores[i]-min));
						if ((yLine != 0 && Math.abs(yLine-y) < 2)) {
							break;
						}
						if (levelScores[i] <= max) {
							g.drawLine(x1, y, x2, y);
						}
						yLine = y;
					}
	
					double x = x1;
					for(int i=0; i<pts.size()-1; i++) {
						g.drawLine((int)x, y1, (int)x, y2);
						x += xStep;
					}
					g.drawLine(x2, y1, x2, y2);
					
					x = x1;
					int x0 = -1;
					int y0 = -1;
					g.setColor(new Color(0xff,0xbb, 0x3f));
					for (Iterator it = pts.iterator(); it.hasNext();) {
						int p = ((Integer)it.next()).intValue();
						int y = (int) (y2 - yStep*(p-min));
						
						if (x0 != -1) {
							g.drawLine(x0, y0, (int) x, y);
						}
						
						x0 = (int) x;
						y0 = y;
						
						x += xStep;
					}
				}
			}
			
			if (!isDrawn) {
				g.setColor(new Color(0x1e, 0x1e, 0x1e));
				String str = "score graph";
				g.drawString(str, x1+5, y2-2*fontHeight);
				str = "no data";
				g.drawString(str, x1+5, y2-1*fontHeight);
			}
		}
	}

	private String getTitle() {
		int level = getLevel(data.score);
		String prefix = CharacterPanel.prefix[(data.isMale ? 1 : 0)][(data.isMagical ? 1 : 0)][(data.isProtected ? 1 : 0)][level];
		String postfix = CharacterPanel.postfix[(data.isMale ? 1 : 0)][(data.isMagical ? 1 : 0)][(data.isProtected ? 1 : 0)][level];
		return (prefix == null ? "" : prefix+" ")+data.name+(postfix == null ? "" : " the "+postfix);
	}

	private int getLevel(int score) {
		int level = 0;
		while(levelScores[level] <= score) {
			level++;
		}
		level--;
		return level;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
		repaint();
	}

	public Dimension getPreferredScrollableViewportSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
		// TODO Auto-generated method stub
		return 1;
	}

	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	public boolean getScrollableTracksViewportWidth() {
		// TODO Auto-generated method stub
		return true;
	}

	public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
		// TODO Auto-generated method stub
		return 1;
	}
}
