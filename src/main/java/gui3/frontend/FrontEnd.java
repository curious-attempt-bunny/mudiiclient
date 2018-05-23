package gui3.frontend;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

public class FrontEnd {
	public static void main(String[] args) {
		JFrame f = new JFrame();
		
		JLabel header = new JLabel("status text here");
		JScrollPane scrollPane = new JScrollPane();
		JList list = new JList(new String[]{"a","b","c"});
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setViewportView(list);
		ListCellRenderer cellRenderer = new CharacterRenderer();
		
		list.setCellRenderer(cellRenderer);
		
		JButton playButton = new JButton("Play");
		JButton manualButton = new JButton("Text-based Menu");
		JButton quitButton = new JButton("Quit");

		GridBagLayout layout = new GridBagLayout();
//		layout.rowHeights = new int[] {40, list.getHeight(), playButton.getHeight()};
		layout.columnWeights = new double[] {1,1,1};
//		layout.rowWeights = new double[] {1, 0.1, 1 };
		
		f.setLayout(layout);

		GridBagConstraints c = new GridBagConstraints();
		
		c.anchor = GridBagConstraints.CENTER;
		
		c.gridwidth = 3;
		
		c.gridx = 0;
		c.gridy = 0;
		c.weighty = 0;
		f.add(header, c);
		
		c.anchor = GridBagConstraints.CENTER;
		c.gridx = 0;
		c.gridy = 1;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		f.add(scrollPane, c);
		
		c.fill = GridBagConstraints.NONE;
		
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER; //SOUTH;
		
		c.gridx = 0;
		c.gridy = 2;
		c.weighty = 0;
		f.add(playButton, c);
		
		c.gridx = 1;
		c.gridy = 2;
		c.weighty = 0;
		f.add(manualButton, c);
		
		c.gridx = 2;
		c.gridy = 2;
		c.weighty = 0;
		f.add(quitButton, c);
		
//		f.pack();
		
		f.setSize(800,600);
		
		f.setVisible(true);
	}
}
