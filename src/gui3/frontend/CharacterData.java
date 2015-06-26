package gui3.frontend;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Vector;

public class CharacterData {

	public String name;
	public int score;
	public boolean isProtected;
	public boolean isMagical;
	public boolean isMale;
	public int stamina;
	public int staminaMax;
	public int magic;
	public int dex;
	public int str;
	private List scoreHistory;
	
	public CharacterData(String name, int score, boolean isMale, boolean isMagical, boolean isProtected, int stamina, int staminaMax, int magic, int str, int dex) {
		this.name = name;
		this.score = score;
		this.isMale = isMale;
		this.isMagical = isMagical;
		this.isProtected = isProtected;
		this.stamina = stamina;
		this.staminaMax = staminaMax;
		this.magic = magic;
		this.str = str;
		this.dex = dex;
	}
	
	public List getScoreHistory() {
		if (scoreHistory == null) {
			scoreHistory = new Vector();
			
			try {
				BufferedReader in = new BufferedReader(new FileReader(name.toLowerCase()+".pts"));
				
				while(true) {
					String line = in.readLine();
					
					if (line == null) {
						break;
					}
					
					Integer pts = Integer.valueOf(line);
					scoreHistory.add(pts);
				}
			} catch (FileNotFoundException e) {
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return scoreHistory;
	}
}
