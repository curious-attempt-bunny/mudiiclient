package login;

import gui3.ComponentWrapper;
import gui3.login.LoginFacade;
import gui3.login.BasicLoginFacade;
import gui3.login.LoginListener;
import gui3.login.LoginStateMachineBuilder;

import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import backend2.InputOutput;

public abstract class LoginScenarioTestCase extends MockObjectTestCase {

	protected BasicLoginFacade loginHandler;
	private Mock mockInputOutput;
	private Mock mockParent;
	private Mock mockLoginListener;

	public LoginScenarioTestCase() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
	
		mockInputOutput = mock(InputOutput.class);
		mockParent = mock(ComponentWrapper.class);
		mockLoginListener = mock(LoginListener.class);
		
		InputOutput inputOutput = (InputOutput) mockInputOutput.proxy();
		ComponentWrapper parent = (ComponentWrapper) mockParent.proxy();
		LoginListener loginListener = (LoginListener) mockLoginListener.proxy();
		
		loginHandler = new BasicLoginFacade();
		loginHandler.setInputOutput(inputOutput);
//		loginHandler.setHostComponent(parent);
		LoginStateMachineBuilder loginStateMachineBuilder = new LoginStateMachineBuilder();
		loginStateMachineBuilder.setCommandSender(inputOutput);
		loginStateMachineBuilder.setHostComponent(parent);
		loginHandler.setLoginStateMachineBuilder(loginStateMachineBuilder); // makes this an integration test
		loginHandler.addLoginListener(loginListener);
		setupLoginHandler();
		
//		mockInputOutput.expects(once()).method("addOutputListener").with( eq(loginHandler) );
		expectState(LoginFacade.STATE_START);
		
//		loginHandler.init();
		
		mockInputOutput.expects(once()).method("connect");
		
		loginHandler.login();
	}

	protected abstract void setupLoginHandler();

	protected void expectResponse(String expectedResponse) {
		mockInputOutput.expects(once()).method("send").with( eq(expectedResponse+"\r") );
	}

	protected void expectState(String loginState) {
		mockLoginListener.expects(once()).method("onLoginState").with( eq(loginState) );
	}

}