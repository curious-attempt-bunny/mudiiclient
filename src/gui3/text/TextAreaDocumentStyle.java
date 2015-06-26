package gui3.text;

import domain.Style;

public class TextAreaDocumentStyle {
	
	int offset;
	Style style;
	TextAreaDocumentStyle next;
	
	TextAreaDocumentStyle(Style style, int offset) {
		this.style = style;
		this.offset = offset;
		next = null;
	}
	
}
