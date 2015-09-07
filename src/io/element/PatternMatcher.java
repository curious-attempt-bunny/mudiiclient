package io.element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatcher implements ElementMatcher {

	private final Pattern pattern;
	private final Matcher matcher;
	private String[] parts;
	private final String pat;

	public PatternMatcher(String pattern) {
		String pat = pattern.replace(".", "\\.").replace("%o",
				"([A-Za-z0-9\\-,' ]*)").replace("%d", "([a-z]*)").replace("%h",
				"(?:hits|misses)").replace("%g", "(?:his|her|its)").replace(
				"%t", "(?:[Tt]he )?([A-Za-z0-9\\-,' ]*)").replace("%a",
				"(?:[Aa](?:n)? )([A-Za-z0-9\\-,' ]*)").replace("%W",
				"([A-Za-z- ]*)");
		this.pat = pat;
		this.pattern = Pattern.compile(pat);
		matcher = this.pattern.matcher("");
	}

	public boolean isMatch(String element) {
		matcher.reset(element);
		boolean found = matcher.lookingAt();
		if (found) {
			parts = new String[matcher.groupCount()];
			// System.err.println("MATCHED : " + element);
			for (int i = 1; i <= matcher.groupCount(); i++) {
				parts[i - 1] = matcher.group(i);
				// System.err.println("PARAM " + i + " : " + matcher.group(i));
			}
		}
		// System.out.println((found ? "match" : "no match") + " of '" + element
		// + "' for " + pat);

		return found;
	}

	public String[] getParts() {
		return parts;
	}

}
