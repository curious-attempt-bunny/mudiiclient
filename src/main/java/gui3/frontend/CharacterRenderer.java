package gui3.frontend;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class CharacterRenderer implements ListCellRenderer {
	CharacterData[] characterData;
	CharacterPanel[] characterPanel;
	
	public CharacterRenderer() {
		characterData = new CharacterData[] {
			new CharacterData("Havoc", 77000, true, true, false, 120, 120, 120, 100, 100),
			new CharacterData("Merlyn", 38000, true, true, false, 105, 105, 105, 100, 100),
			new CharacterData("Poptart", 12800, false, true, true, 100, 100, 100, 100, 100)
		};
		
		characterPanel = new CharacterPanel[] {
			new CharacterPanel(characterData[0]),
			new CharacterPanel(characterData[1]),
			new CharacterPanel(characterData[2])
		};
	}
	
	public Component getListCellRendererComponent(JList list,
	         Object value,
	         int index,
	         boolean isSelected,
	         boolean cellHasFocus) {
//		return new JLabel(new ImageIcon(getClass().getResource("/head.png")));
		
//		return new JLabel(""+index);
		
		characterPanel[index].setSelected(isSelected);
		
		return characterPanel[index];
	}

}
