package login;

import gui3.login.LoginFacade;


public class TypicalLoginScenarioTest extends LoginScenarioTestCase {
	public void testShouldSendLogin() throws Exception {
		expectResponse("mud");
		expectState(LoginFacade.STATE_POST_LOGIN);
		
		loginHandler.onOutputStart();
		loginHandler.onOutput("Please enter either mud or mudguest at the login prompt now. ");
		loginHandler.onOutput("login: ");
		loginHandler.onOutputEnd();
	}
	
	public void testShouldSendAccountID() throws Exception {
		testShouldSendLogin();
		
		expectResponse("Z00012345");
		expectState(LoginFacade.STATE_POST_ACCOUNT_USER);
		
		loginHandler.onOutputStart();
		loginHandler.onOutput("Please enter your account id (starting Z000) now.");
		loginHandler.onOutput("Type QUIT to abort login.");
		loginHandler.onOutput("Account ID: ");
		loginHandler.onOutputEnd();
	}

	public void testShouldSendAccountPassword() throws Exception {
		testShouldSendAccountID();
		
		expectResponse("accPasswd");
		expectState(LoginFacade.STATE_POST_ACCOUNT_PASSWORD);
		
		loginHandler.onOutputStart();
		loginHandler.onOutput("Password: ");
		loginHandler.onOutputEnd();
	}

	protected void setupLoginHandler() {
		loginHandler.setSystemUser("mud");
		loginHandler.setAccountUser("Z00012345");
		loginHandler.setAccountPassword("accPasswd");
	}
	
	public void testShouldRecogniseLogin() throws Exception {
		testShouldSendAccountPassword();
		expectState(LoginFacade.STATE_END);
		expectResponse("/M1\ry");
				
		loginHandler.onOutputStart();
		loginHandler.onOutput("OK\r");
		loginHandler.onOutput("Z00000111 logged in on pts/1.\r");
		loginHandler.onOutput("MUD login menu.\r");
		loginHandler.onOutput("Option (H for help): ");
		loginHandler.onOutputEnd();
	}
}
