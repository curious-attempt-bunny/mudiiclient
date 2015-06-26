package gui3;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

import domain.Configuration;

public class MainWindowWrapper implements ComponentWrapper, FontConsumer,
		ComponentListener, WindowStateListener, WindowListener {

	private JFrame component;

	private Configuration configuration;

	private boolean isSaveChanges;

	public Component getComponent() {
		return component;
	}

	public void init() {
		component = new JFrame() {
			public void setSize(Dimension dimension) {
				super.setSize(new Dimension(Math.max(dimension.width, 2*128), Math.max(dimension.height, 2*128)));
			}
			
			public void setSize(int width, int height) {
				super.setSize(Math.max(width, 2*128), Math.max(height, 2*128));
			}
		};
		component.setTitle("mudiiclient - "+Launcher.VERSION);
//		try {
//			component.setIconImage(new ImageIcon(getClass().getResource("/phdot.gif")).getImage());
//		} catch (Exception e) {
//			
//		}
		isSaveChanges = false;

//		BorderLayout layout = new BorderLayout();
//		component.setLayout(layout);

		component.addComponentListener(this);
		component.addWindowStateListener(this);
		component.addWindowListener(this);
	}

	public void show() {
//		component.pack();
		component.setLocation(configuration.getInt("x", 0), configuration
				.getInt("y", 0));
		component.setSize(configuration.getInt("sizeX", 640), configuration
				.getInt("sizeY", 400));
		// component.setUndecorated(true);
		JComponent contentPane = (JComponent) component.getContentPane();
		contentPane.revalidate();
		component.setVisible(true);
		component.setExtendedState(configuration.getInt("state",
				Frame.MAXIMIZED_BOTH));
		isSaveChanges = true;
	}

	public void setParent(ComponentWrapper componentWrapper) {

	}

	public void setFont(Font font) {
		Dimension size = component.getSize();
		component.pack();
		component.setSize(size);
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void componentMoved(ComponentEvent arg0) {
		if (isSaveChanges) {
			configuration.setInt("x", (int) component.getX());
			configuration.setInt("y", (int) component.getY());
		}
	}

	public void componentResized(ComponentEvent event) {
//		int width = Math.max(2*128, component.getWidth());
//		int height = Math.max(2*128, component.getHeight());
		
		if (isSaveChanges
				&& component.getExtendedState() != Frame.MAXIMIZED_BOTH
				&& component.getSize().getWidth() != 0
				&& component.getSize().getHeight() != 0) {
			configuration.setInt("sizeX", (int) component.getSize().getWidth());
			configuration
					.setInt("sizeY", (int) component.getSize().getHeight());
		}
	}
	
	public void componentShown(ComponentEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void windowStateChanged(WindowEvent event) {
		if (isSaveChanges) {
			configuration.setInt("state", component.getExtendedState());
		}
	}

	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent arg0) {
		
	}

	public void windowClosing(WindowEvent arg0) {
		System.exit(0);
	}

	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
