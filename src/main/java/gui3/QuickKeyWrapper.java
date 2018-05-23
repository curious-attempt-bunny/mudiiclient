package gui3;

import io.listener.StateListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JLabel;

import backend2.CommandHistory;
import backend2.FunctionKeyStore;
import backend2.CommandHistory.Command;
import domain.State;

public class QuickKeyWrapper implements ComponentWrapper, StateListener, FontConsumer, FunctionKeyStore {

	private JLabel component;
	private CommandHistory commandHistory;
	private List commands;
//	private ComponentWrapper mainFrame;
	private boolean isPlaying;
	
	public QuickKeyWrapper() {
		commands = new ArrayList();
	}
	
	public void setCommandHistory(CommandHistory commandHistory) {
		this.commandHistory = commandHistory;
	}

	public Component getComponent() {
		return component;
	}

	public void init() {
		component = new JLabel("No quick keys defined") {
			public void paint(Graphics g) {
				super.paint(g);
				
				Dimension size = getSize();
//				g.setColor(new Color(255,255,255));
				g.drawLine(0, (int)size.getHeight()-1, (int)size.getWidth(), (int)size.getHeight()-1);
			}
			
			public Dimension getMinimumSize() {
				Dimension minimumSize = super.getMinimumSize();
				return new Dimension(minimumSize.width, minimumSize.height+6);
			}
			
			public Dimension getMaximumSize() {
				Dimension minimumSize = super.getMaximumSize();
				return new Dimension(minimumSize.width, minimumSize.height+6);
			}
			
			public Dimension getPreferredSize() {
				Dimension minimumSize = super.getPreferredSize();
				return new Dimension(minimumSize.width, minimumSize.height+6);
			}
		};
		component.setSize(component.getMinimumSize());
		component.setBackground(new Color(0,0,0));
		component.setForeground(new Color(255,255,255));
		component.setVisible(false);
	}

	public void setParent(ComponentWrapper parent) {
		
	}

	public void onState(String key, Object value) {
		if (key == State.KEY_ROOM_SHORT_NAME) {
			List list = commandHistory.getBest((String) value);
			StringBuffer buf = new StringBuffer();
			int i = 1;

			synchronized (commands) {
				commands = list;
				int total = 0;
				for (Iterator iter = commands.iterator(); iter.hasNext();) {
					CommandHistory.Command command = (CommandHistory.Command) iter.next();
					total += command.frequency;
				}
				
	//			buf.append("<html><body><pre>");
				buf.append("  ");
				int req = 3;
				for (Iterator iter = commands.iterator(); iter.hasNext() && req > 0; req--) {
					CommandHistory.Command command = (CommandHistory.Command) iter.next();
					if (i > 1) {
						buf.append("       ");
					}
					
	//				int percentage = (int) Math.ceil((double)command.frequency / total);
	//				if (percentage < 10) {
	//					buf.append("  ");
	//				} else if (percentage < 100) {
	//					buf.append(" ");
	//				}
	//				buf.append(Integer.toString(percentage));
	//				buf.append("% ");
					buf.append("F");
					buf.append(Integer.toString(i));
					buf.append(": ");
					buf.append(command.command);
					
	//				buf.append("<br>");
					i++;
				}
			}
			
//			buf.append("</pre></body></html>");
			component.setText(buf.toString());
//			System.out.println("Fkeys:\n"+buf);
			
			component.setVisible(i > 1 && isPlaying);

//			JFrame frame = (JFrame) mainFrame.getComponent();
//			((JComponent)frame.getContentPane()).revalidate();
//			((JComponent)frame.getContentPane()).repaint();
		} else if (key == State.KEY_PLAYING) {
//			JFrame container = (JFrame) mainFrame.getComponent();
			if (value == Boolean.TRUE) {
				component.setVisible(commands.size() > 0);
				isPlaying = true;
			} else {
				component.setVisible(false);
				isPlaying = false;
			}
			
//			JFrame frame = (JFrame) mainFrame.getComponent();
//			((JComponent)frame.getContentPane()).revalidate();
//			((JComponent)frame.getContentPane()).repaint();
		}
	}

//	public void setMainFrame(ComponentWrapper mainFrame) {
//		this.mainFrame = mainFrame;
//	}

	public void setFont(Font font) {
		component.setFont(font);
	}

	/* (non-Javadoc)
	 * @see gui3.FunctionKeyStore#getFunctionKey(int)
	 */
	public String getFunctionKey(int index) {
		synchronized (commands) {
			if (index <= commands.size() && index >= 1) {
				return ((Command) commands.get(index-1)).command;
			} else {
				return null;
			}
		}
	}
}
