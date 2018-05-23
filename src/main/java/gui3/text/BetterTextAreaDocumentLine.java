package gui3.text;

public class BetterTextAreaDocumentLine {
	int offset;
	int length;
	TextAreaDocumentStyle firstStyle;
	TextAreaDocumentPrefix prefix;

	public BetterTextAreaDocumentLine(int offset, TextAreaDocumentStyle firstStyle, TextAreaDocumentPrefix prefix ) {
		this.offset = offset;
		this.firstStyle = firstStyle;
		length = 0;
	}

	public String toString(byte[] buffer) {
		StringBuffer strBuf = new StringBuffer();
		int i = offset;
		while(i != (offset+length)%buffer.length) {
			strBuf.append((char)buffer[i]);
			i = (i + 1)%buffer.length;
		}
		return strBuf.toString();
	}
	
}
