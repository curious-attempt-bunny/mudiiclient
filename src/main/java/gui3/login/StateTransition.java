package gui3.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import backend2.CommandSender;

public abstract class StateTransition {
	private Pattern pattern;
	protected final CommandSender commandSender;

	protected StateTransition(String pattern, CommandSender commandSender) {
		this.commandSender = commandSender;
		this.pattern = Pattern.compile(".*"+pattern+".*", Pattern.DOTALL | Pattern.MULTILINE);
	}
	
	public boolean isMatch(String text) {
		Matcher matcher = pattern.matcher(text);
		
		return matcher.matches();
	}

	public abstract String execute();
}
