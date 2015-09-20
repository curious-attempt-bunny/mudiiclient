package gui3;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import domain.Configuration;

public class ConfigurationWindowWrapper implements WindowWrapper, ComponentListener, ActionListener {

	private JDialog component;
	private ComponentWrapper parent;
	private Configuration configuration;
	private boolean isSaveChanges;
	private JCheckBox optionLogging;
	private JCheckBox optionEnterResends;
	private JCheckBox optionCommandRemains;
	private JButton buttonOk;
	private JButton buttonCancel;
	private JCheckBox optionActiveDataCollection;
	private JCheckBox optionMaxWidth80;
	private ColourHelper colourHelper;
	private JCheckBox invertedScrolling;
	private JCheckBox autoPlay;

	public Component getComponent() {
		return component;
	}

	public void init() {
		component = new JDialog((JFrame) parent.getComponent());
		component.setLocation(configuration.getInt("config.x", 0), configuration
				.getInt("config.y", 0));
		component.setResizable(false);
		component.setTitle("Configuration options");
		
		component.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("general", createGeneralTab());
		tabbedPane.addTab("colour", createColourPanel());
		component.add(tabbedPane, c);
		
		c.gridwidth = 1;
		
		c.gridx = 0;
		c.gridy = 1;
		c.anchor = GridBagConstraints.EAST;
		buttonOk = new JButton("Ok");
		component.add(buttonOk, c);
		buttonOk.addActionListener(this);
		
		c.gridx = 1;
		c.gridy = 1;
		buttonCancel = new JButton("Cancel");
		component.add(buttonCancel, c);
		buttonCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hide();
			}
		});
		
		component.addComponentListener(this);
		isSaveChanges = true;
	}

	private Container createColourPanel() {
		Container container = new JPanel();
		
		container.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 2;
		container.add(new JLabel("dark"), c);
		
		c.gridx = 0;
		c.gridy = 1;
		container.add(new JLabel("bright"), c);
		
		
		String[] labels = new String[] {
			"", "red", "green", "yellow", "blue", "magenta", "cyan", "white",
			"grey", "red", "green", "yellow", "blue", "magenta", "cyan", "white"
		};
		
		for(int i=8; i<labels.length; i++) {
			c.gridx = i-8+1;
			c.gridy = 0;
			
			container.add(new JLabel(labels[i]), c);
		}
		
		for(int i=1; i<labels.length; i++) {
			c.gridx = 1 + i%8;
			c.gridy = 3 - (1 + i/8);
			
			JButton button = new JButton("change");
			button.setBackground(colourHelper.get(0));
			button.setForeground(colourHelper.get(i));
			container.add(button, c);
			
			final int index = i;
			button.addActionListener(new ActionListener() {
			
				public void actionPerformed(ActionEvent e) {
					onChangeColour(index);
				}
			
			});
		}
		
		return container;
	}

	protected void onChangeColour(int index) {
		Color colour =colourHelper.get(index);
		
		colour = JColorChooser.showDialog(component, "change colour", colour);
		((ConfigurableColourHelper)colourHelper).set(index, colour);
	}

	private Container createGeneralTab() {
		Container container = new JPanel();
		container.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.WEST;
		int y = 0;
		
		c.gridx = 0;
		c.gridy = y++;
		optionLogging = new JCheckBox("automatic logging");
		container.add(optionLogging, c);
		
		c.gridx = 0;
		c.gridy = y++;
		optionEnterResends = new JCheckBox("pressing ENTER with an empty command prompt (while playing) resends the last command");
		container.add(optionEnterResends, c);
		
		c.gridx = 0;
		c.gridy = y++;
		optionCommandRemains = new JCheckBox("last command remains on command prompt after pressing ENTER");
		container.add(optionCommandRemains, c);
	
		c.gridx = 0;
		c.gridy = y++;
		optionActiveDataCollection = new JCheckBox("actively collect data (sends an FES command every 5 seconds)");
		container.add(optionActiveDataCollection, c);

		c.gridx = 0;
		c.gridy = y++;
		optionMaxWidth80 = new JCheckBox("limit the maximum number of characters to a line to 80 (not recommended)");
		container.add(optionMaxWidth80, c);

		c.gridx = 0;
		c.gridy = y++;
		invertedScrolling = new JCheckBox("invert mouse wheel scrolling");
		container.add(invertedScrolling, c);

		c.gridx = 0;
		c.gridy = y++;
		autoPlay = new JCheckBox("auto play on login");
		container.add(autoPlay, c);

		c.gridx = 0;
		c.gridy = y++;
		container.add(new JLabel("Note:"), c);

		c.gridx = 0;
		c.gridy = y++;
		container.add(new JLabel("You can change your font size from within the game using CTRL + and CTRL -"), c);
	
		return container;
	}

	public void setParent(ComponentWrapper parent) {
		this.parent = parent;
	}

	public void show() {
		component.pack();
		component.setVisible(true);
		optionLogging.setSelected(configuration.getInt(Configuration.KEY_LOGGING, Configuration.DEFAULT_LOGGING) == 1);
		optionEnterResends.setSelected(configuration.getInt(Configuration.KEY_ENTER_RESENDS, Configuration.DEFAULT_ENTER_RESENDS) == 1);
		optionCommandRemains.setSelected(configuration.getInt(Configuration.KEY_COMMAND_REMAINS, Configuration.DEFAULT_COMMAND_REMAINS) == 1);
		optionActiveDataCollection.setSelected(configuration.getInt(Configuration.KEY_ACTIVE_DATA_COLLECTION, Configuration.DEFAULT_ACTIVE_DATA_COLLECTION) == 1);
		optionMaxWidth80.setSelected(configuration.getInt(Configuration.KEY_MAX_WIDTH_80, Configuration.DEFAULT_MAX_WIDTH_80) == 1);
		invertedScrolling.setSelected(configuration.getInt(Configuration.KEY_INVERT_MOUSE_WHEEL_SCROLLING, Configuration.DEFAULT_INVERT_MOUSE_WHEEL_SCROLLING) == 1);
		autoPlay.setSelected(configuration.getInt(Configuration.KEY_AUTO_PLAY, Configuration.DEFAULT_AUTO_PLAY) == 1);
		optionLogging.requestFocus();
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void componentMoved(ComponentEvent e) {
		if (isSaveChanges) {
			configuration.setInt("config.x", (int) component.getX());
			configuration.setInt("config.y", (int) component.getY());
		}
	}

	public void componentResized(ComponentEvent e) {
		
	}

	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	private void hide() {
		component.setVisible(false);
		parent.getComponent().requestFocus();
	}
	
	public void actionPerformed(ActionEvent arg0) {
		configuration.setInt(Configuration.KEY_LOGGING, optionLogging.isSelected() ? 1 : 0);
		configuration.setInt(Configuration.KEY_ENTER_RESENDS, optionEnterResends.isSelected() ? 1 : 0);
		configuration.setInt(Configuration.KEY_COMMAND_REMAINS, optionCommandRemains.isSelected() ? 1 : 0);
		configuration.setInt(Configuration.KEY_ACTIVE_DATA_COLLECTION, optionActiveDataCollection.isSelected() ? 1 : 0);
		configuration.setInt(Configuration.KEY_MAX_WIDTH_80, optionMaxWidth80.isSelected() ? 1 : 0);
		configuration.setInt(Configuration.KEY_INVERT_MOUSE_WHEEL_SCROLLING, invertedScrolling.isSelected() ? 1 : 0);
		configuration.setInt(Configuration.KEY_AUTO_PLAY, autoPlay.isSelected() ? 1 : 0);
		hide();
	}

	public void setColourHelper(ColourHelper colourHelper) {
		this.colourHelper = colourHelper;
	}
}
