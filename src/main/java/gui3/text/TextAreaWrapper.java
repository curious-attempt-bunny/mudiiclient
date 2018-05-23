package gui3.text;

import gui3.ColourHelper;
import gui3.ComponentWrapper;
import gui3.FontConsumer;
import io.listener.StateListener;
import io.listener.StyleListener;

import java.awt.Component;
import java.awt.Font;

import backend2.OutputListener;
import domain.Configuration;
import domain.Style;
import io.sensor.PlanSensor;

public class TextAreaWrapper implements ComponentWrapper, OutputListener, StyleListener, FontConsumer, PrefixListener, StateListener {

	private RobustTextAreaView component;
	private ColourHelper colourHelper;
	private boolean isScrollback;
	private BetterTextAreaDocument document;
	private Configuration configuration;
	private ScrollbackController scrollbackController;
	private PlanSensor plan;

	public void onState(String key, Object value) {

	}
	
	public void setDocument(BetterTextAreaDocument document) {
		this.document = document;
	}

	public Component getComponent() {
		return component;
	}

	public void init() {
		component = new RobustTextAreaView(colourHelper, document, scrollbackController);
		component.setPlan(plan);
		component.setScrollback(isScrollback);
		component.setConfiguration(configuration);
	}

	public void onOutput(String text) {
		if (!isScrollback /*&& isPlaying*/) {
			document.append(text);
		}
	}

	public void onOutputEnd() {
		component.repaint(50);
	}

	public void onOutputStart() {
		// TODO Auto-generated method stub
		
	}

	public void onStyle(Style style) {
		if (!isScrollback /*&& isPlaying*/) {
			document.onStyle(style);
		}
	}

	public void setFont(Font font) {
		component.setFont(font);
	}

	public void setColourHelper(ColourHelper colourHelper) {
		this.colourHelper = colourHelper;
	}

	public void setScrollback(boolean isScrollback) {
		this.isScrollback = isScrollback;
	}

	public void onPrefix(TextAreaDocumentPrefix areaDocumentPrefix) {
		document.setPrefix(areaDocumentPrefix);
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
		
	}

	public void setScrollbackController(ScrollbackController scrollbackController) {
		this.scrollbackController = scrollbackController;
	}

	public void setPlan(PlanSensor plan) {
		this.plan = plan;
	}
}
