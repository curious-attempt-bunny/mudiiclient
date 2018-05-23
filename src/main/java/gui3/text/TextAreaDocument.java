package gui3.text;

import java.util.ListIterator;

import domain.Style;

public interface TextAreaDocument {

	public abstract void append(String text);

	public abstract ListIterator lines(int lineIndex, int lineCount);

	public abstract void onStyle(Style style);

	public abstract void setLineWidth(int lineWidth);

	public abstract void setPrefix(TextAreaDocumentPrefix areaDocumentPrefix);

}