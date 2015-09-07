package io.element;

public interface ElementMatcher {
	boolean isMatch(String element);

	String[] getParts();
}
