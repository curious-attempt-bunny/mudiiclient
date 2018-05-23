package gui3.login;

import domain.Configuration;
import gui3.ComponentWrapper;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import backend2.InputOutput;

public class BasicLoginFacade implements LoginFacade {
	private InputOutput inputOutput;
	private String accountPassword;
	private String accountUser;
	private String systemPassword;
	private String systemUser;
	private StringBuffer buffer;
	private String state;
	private ComponentWrapper hostComponent;
	private List loginListeners;
	private FiniteStateMachine finiteStateMachine;
	private LoginStateMachineBuilder loginStateMachineBuilder;
	private Configuration configuration;

	public BasicLoginFacade() {
		loginListeners = new Vector();
		
		buffer = new StringBuffer();
		
		loginStateMachineBuilder = new LoginStateMachineBuilder();
	}
	
	public void setHostComponent(ComponentWrapper hostComponent) {
		this.hostComponent = hostComponent;
	}

	public void onOutput(String text) {
		buffer.append(text);
	}

	public void onOutputEnd() {
		String buf = buffer.toString();
		
		StateTransition matchingTransition = findMatchingTransition(state, buf);
		
		if (matchingTransition != null) {
			buffer.delete(0, buffer.length());
			fireOnLoginState(matchingTransition.execute());
		}
	}

	private StateTransition findMatchingTransition(String state, String triggerText) {
		return finiteStateMachine.findMatchingTransition(state, triggerText);
	}

	public void onOutputStart() {
		// TODO Auto-generated method stub

	}

	public void setSystemUser(String systemUser) {
		this.systemUser = systemUser;
	}

	public void setSystemPassword(String systemPassword) {
		this.systemPassword = systemPassword;
	}

	public void setAccountUser(String accountUser) {
		this.accountUser = accountUser;
	}

	public void setAccountPassword(String accountPassword) {
		this.accountPassword = accountPassword;
	}
	
	public void login() {
		loginStateMachineBuilder.setCommandSender(inputOutput);
		loginStateMachineBuilder.setHostComponent(hostComponent);
		loginStateMachineBuilder.setConfiguration(configuration);
		
		LoginDetails loginDetails = new LoginDetails();
		loginDetails.setAccountPassword(accountPassword);
		loginDetails.setAccountUser(accountUser);
		loginDetails.setSystemPassword(systemPassword);
		loginDetails.setSystemUser(systemUser);
		
		finiteStateMachine = loginStateMachineBuilder.build(loginDetails);
		
		fireOnLoginState(STATE_START);
		
		inputOutput.connect();
	}

	public void init() {
		
	}


	private void fireOnLoginState(String state) {
		if (state != this.state) {
			this.state = state;
			System.out.println("STATE: "+state);
			for (Iterator it = loginListeners.iterator(); it.hasNext();) {
				LoginListener loginListener = (LoginListener) it.next();
				loginListener.onLoginState(state);
			}
		}
	}

	public void setInputOutput(InputOutput inputOutput) {
		this.inputOutput = inputOutput;
	}

	public void addLoginListener(LoginListener loginListener ) {
		loginListeners.add(loginListener);
	}

	public void setLoginStateMachineBuilder(
			LoginStateMachineBuilder loginStateMachineBuilder) {
		this.loginStateMachineBuilder = loginStateMachineBuilder;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
}
