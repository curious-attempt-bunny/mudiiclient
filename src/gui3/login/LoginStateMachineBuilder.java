package gui3.login;

import gui3.ComponentWrapper;
import backend2.CommandSender;
import backend2.SimpleLoginTrigger;

public class LoginStateMachineBuilder {

	private CommandSender commandSender;
	private ComponentWrapper hostComponent;
	
	public FiniteStateMachine build(LoginDetails loginDetails) {
		FiniteStateMachine finiteStateMachine = new FiniteStateMachine();

		StateTransition transitionEnterClientMode = createTransition("Option", "/F\r/M1\ry", LoginFacade.STATE_END);
		StateTransition transitionFinishNews = createTransition("Hit return.", "", LoginFacade.STATE_POST_NEWS);
		StateTransition transitionSkipNews = createTransition("Skip the rest\\? \\(y/n\\)", "n", LoginFacade.STATE_POST_SKIP_NEWS_ITEM);
		StateTransition transitionAccountPassword = createTransition("Account ID:", loginDetails.getAccountUser(), LoginFacade.STATE_POST_ACCOUNT_USER);
		
		finiteStateMachine.addTransition(LoginFacade.STATE_START, createTransition("login:", loginDetails.getSystemUser(), LoginFacade.STATE_POST_LOGIN));
		
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_LOGIN, transitionEnterClientMode);
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_LOGIN, createTransition("Password:", loginDetails.getSystemPassword(), LoginFacade.STATE_POST_SYSTEM_PASSWORD));
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_LOGIN, transitionSkipNews);
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_LOGIN, createTransition("Hit return.", "", LoginFacade.STATE_POST_SKIP_NEWS_ITEM));

		finiteStateMachine.addTransition(LoginFacade.STATE_POST_SYSTEM_PASSWORD, createTransition("@tesuji:~\\$", "/usr/bin/mudlogin", LoginFacade.STATE_POST_SYSTEM_PASSWORD));
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_SYSTEM_PASSWORD, transitionAccountPassword);
		
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_LOGIN, transitionAccountPassword);
		
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_ACCOUNT_USER, createTransition("Password:", loginDetails.getAccountPassword(), LoginFacade.STATE_POST_ACCOUNT_PASSWORD));
		
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_ACCOUNT_PASSWORD, transitionEnterClientMode);
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_ACCOUNT_PASSWORD, transitionSkipNews);
		
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_ACCOUNT_PASSWORD, transitionFinishNews);
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_ACCOUNT_PASSWORD, new LoginSuperseedUserPromptTransition("Do you want to supersede this other session?", commandSender, hostComponent ));
		
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_SKIP_NEWS_ITEM, transitionFinishNews);
		finiteStateMachine.addTransition(LoginFacade.STATE_POST_SKIP_NEWS_ITEM, transitionEnterClientMode);

		finiteStateMachine.addTransition(LoginFacade.STATE_POST_NEWS, transitionEnterClientMode);
	
		return finiteStateMachine;
	}

	private StateTransition createTransition(String pattern, String response, String destinationState) {
		return new SimpleLoginTrigger(pattern, commandSender, response, destinationState);
	}

	public void setCommandSender(CommandSender commandSender) {
		this.commandSender = commandSender;
	}

	public void setHostComponent(ComponentWrapper hostComponent) {
		this.hostComponent = hostComponent;
	}

}
