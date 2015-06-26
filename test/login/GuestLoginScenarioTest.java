package login;

import gui3.login.LoginFacade;


public class GuestLoginScenarioTest extends LoginScenarioTestCase {
	protected void setupLoginHandler() {
		loginHandler.setSystemUser("mudguest");
		loginHandler.setAccountUser(null);
		loginHandler.setAccountPassword(null);
	}
	
	public void testShouldSendLogin() throws Exception {
		expectResponse("mudguest");
		expectState(LoginFacade.STATE_POST_LOGIN);
		
		loginHandler.onOutputStart();
		loginHandler.onOutput("Please enter either mud or mudguest at the login prompt now.\r");
		loginHandler.onOutput("login: ");
		loginHandler.onOutputEnd();
	}

	public void testShouldRecogniseLogin() throws Exception {
		testShouldSendLogin();
		expectState(LoginFacade.STATE_END);
		expectResponse("/M1\ry");
				
		loginHandler.onOutputStart();
		loginHandler.onOutput("   Logged in with guest privileges on pts/2.\r");
		loginHandler.onOutput("MUD login menu.\r");
		loginHandler.onOutput("Option (H for help): ");
		loginHandler.onOutputEnd();
	}

	
}
