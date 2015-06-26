package gui3.login;

import gui3.ComponentWrapper;
import backend2.InputOutput;
import backend2.OutputListener;

public interface LoginFacade extends OutputListener {

	String STATE_START = "Connecting...";

	String STATE_POST_LOGIN = "Sent system user...";

	String STATE_POST_SYSTEM_PASSWORD = "Send system password...";

	String STATE_POST_ACCOUNT_USER = "Sent account user...";

	String STATE_POST_ACCOUNT_PASSWORD = "Sent account password...";

	String STATE_POST_SKIP_NEWS_ITEM = "Read news...";

	String STATE_EXAMINING_ACCOUNT = "Examining acount...";

	String STATE_EXAMINED_ACCOUNT = "Examined account...";

	String STATE_END = "Entered client mode...";

	String STATE_POST_NEWS = "Done reading news...";

	void setHostComponent(ComponentWrapper parent);

	void setSystemUser(String systemUser);

	void setSystemPassword(String systemPassword);
	
	void setAccountUser(String accountUser);

	void setAccountPassword(String accountPassword);

	void login();

	void init();

	void setInputOutput(InputOutput inputOutput);

	void addLoginListener(LoginListener loginListener);

}