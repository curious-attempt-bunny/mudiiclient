package gui3;

import java.awt.Color;

public class ConfigurableColourHelper implements ColourHelper {

	private final ColourHelper colourHelper;

	private Color[] colour = new Color[16];
	
	public ConfigurableColourHelper(ColourHelper colourHelper) {
		this.colourHelper = colourHelper;
	}
	
	public Color get(int style_index) {
		if (colour[style_index] == null) {
			return colourHelper.get(style_index);
		} else {
			return colour[style_index];
		}
	}

	public void set(int style_index, Color colour) {
		this.colour[style_index] = colour;
	}

}
