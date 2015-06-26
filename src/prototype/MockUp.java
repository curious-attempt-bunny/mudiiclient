package prototype;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class MockUp extends JPanel implements ActionListener {
	private static final int BAR_MIN = 26-5;
	private static final int BAR_MAX = 291+2;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new MockUp());
		frame.setSize(600,400);
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}

	private Image topLeft;
	private Image topRight;
	private Image bottomLeft;
	private Image bottomRight;
	private Image top;
	private Image left;
	private Image right;
	private Image bottom;
	private int w;
	private int h;
	private Image staminaFull;
	private Image staminaEmpty;
	private Image magicFull;
	private Image magicEmpty;
	private int stamina;
	private int staminaMax;
	private int magic;
	private int md;
	private int sd;
	private Timer timer;
	
	public MockUp() {
		topLeft = new ImageIcon(MockUp.class.getResource("/gdn/TopLeftCorner.gif")).getImage();
		topRight = new ImageIcon(MockUp.class.getResource("/gdn/TopRightCorner.gif")).getImage();
		bottomLeft = new ImageIcon(MockUp.class.getResource("/gdn/BottomLeftCorner.gif")).getImage();
		bottomRight = new ImageIcon(MockUp.class.getResource("/gdn/BottomRightCorner.gif")).getImage();
		top = new ImageIcon(MockUp.class.getResource("/gdn/TopWall.gif")).getImage();
		left = new ImageIcon(MockUp.class.getResource("/gdn/LeftWall.gif")).getImage();
		right = new ImageIcon(MockUp.class.getResource("/gdn/RightWall.gif")).getImage();
		bottom = new ImageIcon(MockUp.class.getResource("/gdn/BottomWall.gif")).getImage();
		
		staminaFull = new ImageIcon(MockUp.class.getResource("/gdn/HealthBarFull.gif")).getImage();
		staminaEmpty = new ImageIcon(MockUp.class.getResource("/gdn/HealthBarEmpty.gif")).getImage();
		
		magicFull = new ImageIcon(MockUp.class.getResource("/gdn/MagicBarFull.gif")).getImage();
		magicEmpty = new ImageIcon(MockUp.class.getResource("/gdn/MagicBarEmpty.gif")).getImage();
		
		stamina = 87;
		staminaMax = 120;
		magic = 25;
		
		md = -5;
		sd = +3;
	}
	
	public void paint(Graphics g) {
		super.paint(g);
	
		g.setColor(new Color(0,0,0));
	
		double st = stamina/(double)staminaMax;
		double ma = magic/(double)staminaMax;
		
		int bw = magicFull.getWidth(null);
		int bh = magicFull.getHeight(null);
		
		double scale = 1.0;
		double cScale = 1.0;
		
		w = getWidth();
		h = getHeight();
		double fX ;
		if (w < 400) {
			fX = 0.5;
		} else if (w >= 600) {
			fX = 1.0;
		} else {
			fX = 0.5+(w-400)*0.5/200;
		}
		double fY ;
		if (h < 400) {
			fY = 0.5;
		} else if (h >= 600) {
			fY = 1.0;
		} else {
			fY = 0.5+(h-400)*0.5/200;
		}
		
		scale = Math.min(fX, fY);
		cScale = scale;
		
//		if (getWidth() < 600 || getHeight() < 600) {
//			scale = 0.5;
//			cScale = 0.5;
//		}
		
		g.fillRect(0,0,w,h);
	
		int c = (int)(topRight.getWidth(null) * cScale);
		int bb = (int)(bottom.getHeight(null) * scale);
		int br = (int)(right.getWidth(null) * scale);
		
		double bScale = 1.6*c/(double)bw;
		
		drawRepeated(g, top, 0, 0, scale, 1, 0);
		drawRepeated(g, left, 0, 0, scale, 0, 1);
		drawRepeated(g, right, w-br, 0, scale, 0, 1);
		drawRepeated(g, bottom, 0, h-bb, scale, 1, 0);
	
		drawScaled(g, topLeft, 0, 0, cScale);
		drawScaled(g, topRight, w-c, 0, cScale);
		drawScaled(g, bottomLeft, 0, h-c, cScale);
		drawScaled(g, bottomRight, w-c, h-c, cScale);
		
		int bhs = (int) (bh*bScale);
		int bws = (int)(bw*bScale);
		
		drawBar(g, staminaEmpty, staminaFull, 0, h-bhs, bScale, st);
		drawBar(g, magicEmpty, magicFull, w-bws, h-bhs, bScale, ma);
		
		if (timer == null) {
			timer = new Timer(200, this);
			timer.setRepeats(true);
			timer.start();
		}
	}

	private void drawBar(Graphics g, Image empty, Image full, int x, int y, double scale, double inverseRatio) {
		double ratio = 1-inverseRatio;
		
		
		
		int width = empty.getWidth(null);
		int sw = (int)(width*scale);
		int height = empty.getHeight(null);
		int sh = (int)(height*scale);
		
		double sourceCut = (BAR_MIN+(BAR_MAX-BAR_MIN)*ratio); 
		double destCut = sourceCut*sh/height;
		
//		System.out.println(inverseRatio+"% = sourceCut "+sourceCut+" destCut "+destCut);
		
		g.drawImage(full, x, y, x+sw, y+sh, 0, 0, width, height, null);
		g.drawImage(empty, x, y, x+sw, (int) (y+destCut), 0, 0, width, (int) sourceCut, null);
	}

	private void drawScaled(Graphics g, Image image, int x, int y, double scale) {
		int sw = (int)(image.getWidth(null)*scale);
		int sh = (int)(image.getHeight(null)*scale);
		g.drawImage(image, x, y, sw, sh, null);
	}

	private void drawRepeated(Graphics g, Image image, int x, int y, double scale, int dx, int dy) {	
		int sw = (int)(image.getWidth(null)*scale);
		int sh = (int)(image.getHeight(null)*scale);
		while (x < w && y < h) {
			g.drawImage(image, x, y, sw, sh, null);
			
			x += dx*sw;
			y += dy*sh;
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		stamina = Math.min(Math.max(0,stamina+sd), staminaMax);
		magic = Math.min(Math.max(0,magic+md), staminaMax);
		
		if (stamina == 0 || stamina == staminaMax) {
			sd = -sd;
		}
		if (magic == 0 || magic == staminaMax) {
			md = -md;
		}
		
		repaint();
	}
}
