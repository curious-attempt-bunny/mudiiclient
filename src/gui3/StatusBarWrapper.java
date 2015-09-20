package gui3;

import gui3.layout.LayoutAware;
import gui3.layout.WindowLayout;
import io.listener.StateListener;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import domain.State;
import domain.Style;

public class StatusBarWrapper implements ComponentWrapper, StateListener,
		FontConsumer, LayoutAware {

//	private ComponentWrapper parent;

	private State state;

	private Color pointsColour;

	private Color color100;

	private Color color76;

	private Color color36;

	private Color color16;

	private Color color6;

	private Color color0;

	private StatusBar component;

	private ColourHelper colourHelper;

	private Color resetColour;

	private Color colorWhite;

	private ComponentWrapper parent;

	private WindowLayout layout;

	public StatusBarWrapper() {

	}

	public void setParent(ComponentWrapper parent) {
		this.parent = parent;
	}

	public void init() {
		component = new StatusBar();

		component.setVisible(false);
		
//		Container container = ((JFrame) parent.getComponent()).getContentPane();
//		container.add(component, BorderLayout.NORTH);
		
		state.addStateListener(this);

		pointsColour = new Color(0xa8, 0x54, 0x00);
		resetColour = color100 = colourHelper.get(Style.COLOUR_BLUE);
		colorWhite = colourHelper.get(Style.COLOUR_LT_WHITE);
		color100 = colourHelper.get(Style.COLOUR_LT_GREEN);
		color76 = colourHelper.get(Style.COLOUR_GREEN);
		color36 = colourHelper.get(Style.COLOUR_LT_YELLOW);
		color16 = colourHelper.get(Style.COLOUR_YELLOW);
		color6 = colourHelper.get(Style.COLOUR_RED);
		color0 = colourHelper.get(Style.COLOUR_LT_RED);
	}

	public void setFont(Font font) {
		component.setFont(font);
	}

	public void setState(State state) {
		this.state = state;
	}

	public void onState(String key, Object value) {
		if (key == State.KEY_PLAYING) {
			if (value == Boolean.TRUE) {
				component.setVisible(true);
				
				// additional work-around for bizarre failure to resize the scroll bar
				// seems to be a timing issue
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					
				}
				
				layout.doLayout();
			} else {
				component.setVisible(false);
			}
		} else {
			boolean emphasize = false;
			String text = (value == null ? "" : value.toString());
			if (key == State.KEY_POINTS && text.length() > 3) {
				text = text.substring(0, text.length() - 3) + ","
						+ text.substring(text.length() - 3, text.length());
			}
			Color colour = color0;
			if (key == State.KEY_POINTS) {
				colour = pointsColour;
			} else if (value instanceof Integer) {
				if (isEffectiveKey(key)) {
					int ratio = getRatioForKey(key, (Integer) value);
					colour = getColorForRatio(ratio);
					if (!(key == State.KEY_MAGIC && ((Integer)value).intValue() == 0)) {
						emphasize = getEmphasisForRatio(ratio);
					}
				} else {
					colour = color100;
					if (key == State.KEY_STAMINA_MAX) {
						fireEffectiveState(State.KEY_STAMINA);
						fireEffectiveState(State.KEY_MAGIC);
					} else if (key == State.KEY_DEXTERITY) {
						fireEffectiveState(State.KEY_DEXTERITY_EFFECTIVE);
					} else if (key == State.KEY_STRENGTH) {
						fireEffectiveState(State.KEY_STRENGTH_EFFECTIVE);
					}
					
					if (key == State.KEY_RESET_TIME) {
						if (value != null && ((Integer)value).intValue() <= 6) {
							colour = color0;
							emphasize = true;
						} else {
							colour = resetColour;
						}
					}
				}
			} else if (value instanceof Boolean) {
				colour = color0;
				if (((Boolean) value).booleanValue()) {
					if (key == State.KEY_BLIND) {
						text = "Blind";
					} else if (key == State.KEY_CRIPPLED) {
						text = "Crippled";
					} else if (key == State.KEY_DEAF) {
						text = "Deaf";
					} else if (key == State.KEY_DUMB) {
						text = "Dumb";
					}
				} else {
					text = "";
				}
			} else if (value instanceof String) {
				colour = color100; // ???
				
				if (key == State.KEY_WEATHER) {
					if ("sunny".equals(value)) {
						colour = color100;
					} else {
						colour = color0;
					}
				} else if (key == State.KEY_CHART_DIRS) {
					colour = colorWhite;
				}
				
				text = (String) value;
			}

			component.setItem(key, colour, text, emphasize);
			component.repaint();
		}
	}

	private void fireEffectiveState(String keyEffective) {
		Object value = state.get(keyEffective);
		if (value != null) {
			onState(keyEffective, value);
		}
	}

	private boolean isEffectiveKey(String key) {
		return key == State.KEY_STAMINA || key == State.KEY_DEXTERITY_EFFECTIVE
				|| key == State.KEY_STRENGTH_EFFECTIVE
				|| key == State.KEY_MAGIC;
	}

	private Color getColorForRatio(int ratio) {
		Color colour;
		if (ratio == 0 || ratio >= 100) {
			colour = color100;
		} else if (ratio > 76) {
			colour = color76;
		} else if (ratio > 36) {
			colour = color36;
		} else if (ratio > 16) {
			colour = color16;
		} else if (ratio > 6) {
			colour = color6;
		} else {
			colour = color0;
		}
		return colour;
	}

	private boolean getEmphasisForRatio(int ratio) {
		return (ratio <= 36);
	}

	private int getRatioForKey(String key, Integer value) {
		Integer max = null;
		if (key == State.KEY_STAMINA) {
			max = (Integer) state.get(State.KEY_STAMINA_MAX);
		} else if (key == State.KEY_MAGIC) {
			max = (Integer) state.get(State.KEY_STAMINA_MAX);
		} else if (key == State.KEY_DEXTERITY_EFFECTIVE) {
			max = (Integer) state.get(State.KEY_DEXTERITY);
		} else if (key == State.KEY_STRENGTH_EFFECTIVE) {
			max = (Integer) state.get(State.KEY_STRENGTH);
		}

		if (max == null) {
			return 100;
		} else {
			return (value.intValue() * 100) / max.intValue();
		}
	}

	public void setColourHelper(ColourHelper colourHelper) {
		this.colourHelper = colourHelper;
	}

	public Component getComponent() {
		return component;
	}

	public void setLayout(WindowLayout layout) {
		this.layout = layout;
	}

}
