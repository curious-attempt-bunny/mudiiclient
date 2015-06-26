package backend2;

import gui3.login.StateTransition;

public class SimpleLoginTrigger extends StateTransition {

	private final String response;
	private final String state;

	public SimpleLoginTrigger(String pattern, CommandSender commandSender, String response, String state) {
		super(pattern, commandSender);
		this.response = response;
		this.state = state;
	}
	
	public String execute() {
		commandSender.send(response+"\r");
		return state;
	}

}
