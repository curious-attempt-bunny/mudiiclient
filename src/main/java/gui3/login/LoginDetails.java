package gui3.login;

public class LoginDetails {

	private String accountPassword;
	private String accountUser;
	private String systemPassword;
	private String systemUser;

	public String getSystemUser() {
		return systemUser;
	}

	public String getSystemPassword() {
		return systemPassword;
	}

	public String getAccountUser() {
		return accountUser;
	}

	public String getAccountPassword() {
		return accountPassword;
	}

	public void setAccountPassword(String accountPassword) {
		this.accountPassword = accountPassword;
	}

	public void setAccountUser(String accountUser) {
		this.accountUser = accountUser;
	}

	public void setSystemPassword(String systemPassword) {
		this.systemPassword = systemPassword;
	}

	public void setSystemUser(String systemUser) {
		this.systemUser = systemUser;
	}

}
