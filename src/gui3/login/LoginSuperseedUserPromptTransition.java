package gui3.login;

import gui3.ComponentWrapper;

import javax.swing.JOptionPane;

import backend2.CommandSender;

public class LoginSuperseedUserPromptTransition extends StateTransition {
	private final ComponentWrapper hostComponent;

	public LoginSuperseedUserPromptTransition(String pattern, CommandSender commandSender, ComponentWrapper hostComponent) {
		super(pattern, commandSender);
		this.hostComponent = hostComponent;
	}

	public String execute() {
		int optionPane = JOptionPane.showConfirmDialog(hostComponent.getComponent(), "MUDII thinks you are already logged in.\nDo you want to log in and disconnect\nyour existing session?", "Already logged in", JOptionPane.OK_CANCEL_OPTION);
		if (optionPane == JOptionPane.OK_OPTION) {
			commandSender.send("y\r");
			return LoginFacade.STATE_POST_ACCOUNT_PASSWORD;
		} else {
			commandSender.send("n\r");
			return LoginFacade.STATE_POST_ACCOUNT_PASSWORD;
		}
	}
}
