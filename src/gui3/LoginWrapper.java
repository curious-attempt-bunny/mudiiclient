package gui3;

import gui3.layout.GameWindowLayout;
import gui3.login.LoginFacade;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import backend2.BareBonesBrowserLaunch;
import domain.Configuration;

public class LoginWrapper implements ComponentWrapper {
	private Color fg = new Color(0xff,0xbb, 0x3f);
	private Color bg = new Color(0,0,0x4e);
	private JPanel component;
	private LoginFacade loginHandler;
	private JTextField accountUser;
	private JPasswordField accountPassword;
	private JCheckBox rememberLogin;
	private GameWindowLayout gameWindowLayout;
	private Configuration configuration;
	private String host;
	
	public void setHost(String host) {
		this.host = host;
	}

	public void init() {
		ActionListener loginActionListener = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				loginAction();
			}
		
		};
		
		ActionListener guestloginActionListener = new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				guestloginAction();
			}
		
		};
		
		component = new JPanel();
		component.setBackground(bg);
		component.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(5, 5, 5, 5);

		ImageIcon imageIcon1 = new ImageIcon(getClass().getResource("/title.gif"));
		ImageIcon imageIcon3 = new ImageIcon(getClass().getResource("/sundragon2.jpg"));
		ImageIcon imageIcon4 = new ImageIcon(getClass().getResource("/mt.gif"));
		
		int y = 0;
		
		c.gridx=0;
		c.gridy=y++;
		c.gridwidth=6;
		component.add(addToolTipText("visit mudii.co.uk", makeBrowsable("http://mudii.co.uk", new JLabel(imageIcon1))), c);
		
		c.gridwidth=1;
		
		c.gridx=0;
		c.gridy=y;
		c.gridheight=9;
		c.gridwidth=2;
		component.add(addToolTipText("visit mudii.co.uk", makeBrowsable("http://mudii.co.uk",new JLabel(imageIcon3))), c);
		
		c.gridheight=1;
		c.gridwidth=1;
		
		c.anchor=GridBagConstraints.WEST;
		
		c.gridx=3;
		c.gridy=y;
		component.add(createLabel("Account ID:"), c);
		
		c.gridx=4;
		c.gridy=y++;
		accountUser = new JTextField(12);
		component.add(accountUser, c);
		accountUser.addActionListener(loginActionListener);
		
		c.gridx=3;
		c.gridy=y;
		component.add(createLabel("Password:"), c);
		
		c.gridx=4;
		c.gridy=y++;
		accountPassword = new JPasswordField(12);
		component.add(accountPassword, c);
		accountPassword.addActionListener(loginActionListener);
		
		c.gridx=3;
		c.gridy=y++;
		c.gridwidth=2;
		rememberLogin = createCheckBox();
		component.add(rememberLogin, c);
//		rememberLogin.addActionListener(this);
		
//		c.gridwidth=1;
//
//		c.gridx=3;
//		c.gridy=y;
//		component.add(addToolTipText("visit mudii.co.uk to create an account", createLink("Create an account", "http://www.mudii.co.uk/newaccount.php")), c);
////		 http://www.mudii.co.uk/newaccount.php
//
//		c.gridx=4;
//		c.gridy=y++;
//		c.anchor=GridBagConstraints.EAST;
//		JButton loginButton = createButton("Login");
//		component.add(loginButton, c);
//		loginButton.addActionListener(loginActionListener);
//
//		c.anchor=GridBagConstraints.WEST;
//
//		c.gridx=3;
//		c.gridy=y;
//		component.add(addToolTipText("contact the administrator for your account details", createLink("Request lost account details", "http://www.mudii.co.uk/contactus.php")), c);
//
//		c.gridx=4;
//		c.gridy=y++;
//		c.anchor=GridBagConstraints.EAST;
//		JButton guestLoginButton = createButton("Guest Login");
//		component.add(guestLoginButton, c);
//		guestLoginButton.addActionListener(guestloginActionListener);
//
//		c.anchor=GridBagConstraints.WEST;
//
//		c.gridx=3;
//		c.gridy=y++;
//		component.add(addToolTipText("straight from the author's mouth", createLink("Beginners Companion", "http://mud.co.uk/muse/begscomp.htm")), c);
//
//		c.gridx=3;
//		c.gridy=y;
//		component.add(addToolTipText("find out more about mud at muddled-times.com", createLink("Get in the know, read the MT:", "http://www.muddled-times.com/issue.fod")), c);
//
//		c.gridx=4;
//		c.gridy=y++;
//		component.add(addToolTipText("find out more about mud at muddled-times.com", makeBrowsable("http://www.muddled-times.com/issue.fod", new JLabel(imageIcon4))), c);
//
//		c.gridx=3;
//		c.gridy=y;
//		component.add(addToolTipText("makes yourself heard on the forums", createLink("Discuss mud topics on the forums", "http://www.mudii.co.uk/forums/")), c);
//
//
		// 
		
		if (configuration.getInt(host+".login.remember", 1) == 1) {
			rememberLogin.setSelected(true);
			accountUser.setText(configuration.getSetting(host+".account.user", ""));
			accountPassword.setText(unscramble(configuration.getSetting(host+".account.password", "")));
		}
		
		highlightComponent(accountUser, false);
		highlightComponent(accountPassword, false);
	}

	private String unscramble(String str) {
//		StringBuffer buf = new StringBuffer();
//		for(int i=0; i<str.length(); i++) {
//			char ch = str.charAt(i);
//			if (Character.isLetter(ch)) {
//				if (Character.isUpperCase(ch)) {
//					ch = (char) (((ch-'A')-1)%26 + 'A');
//				} else {
//					ch = (char) (((ch-'a')-2)%26 + 'a');
//				}
//			} else if (Character.isDigit(ch)) {
//				ch = (char) (((ch-'0')-3)%10 + '0');
//			}
//			buf.insert(0, ch);
//		}
//		return buf.toString();
		return str;
	}

	private String scramble(String str) {
//		StringBuffer buf = new StringBuffer();
//		for(int i=0; i<str.length(); i++) {
//			char ch = str.charAt(i);
//			if (Character.isLetter(ch)) {
//				if (Character.isUpperCase(ch)) {
//					ch = (char) (((ch-'A')+1)%26 + 'A');
//				} else {
//					ch = (char) (((ch-'a')+2)%26 + 'a');
//				}
//			} else if (Character.isDigit(ch)) {
//				ch = (char) (((ch-'0')+3)%10 + '0');
//			}
//			buf.insert(0, ch);
//		}
//		return buf.toString();
		return str;
	}

	public Component getComponent() {
		return component;
	}

	public void setParent(ComponentWrapper parent) {
		// TODO Auto-generated method stub

	}

	private static JComponent addToolTipText(String text, JComponent component) {
		component.setToolTipText(text);
		return component;
	}
	
	private static JComponent createLink(String text, final String url) {
		final JLabel link = new JLabel("<html><body><a href=#>"+text+"</a></body></html>");
		makeBrowsable(url, link);
		return link;
	}

	private static JComponent makeBrowsable(final String url, final JComponent component) {
		component.addMouseListener(new MouseListener() {
		
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
		
			}
		
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
		
			}
		
			public void mouseExited(MouseEvent e) {
				component.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		
			public void mouseEntered(MouseEvent e) {
				component.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
		
			public void mouseClicked(MouseEvent e) {
				BareBonesBrowserLaunch.openURL(url);
			}
		
		});
		
		return component;
	}

	private JButton createButton(String str) {
		JButton button = new JButton(str);
		return button;
	}

	private JCheckBox createCheckBox() {
		JCheckBox checkBox = new JCheckBox("remember account details");
		checkBox.setBackground(bg);
		checkBox.setForeground(fg);
		return checkBox;
	}

	private JLabel createLabel(String str) {
		JLabel label = new JLabel(str);
		label.setForeground(fg);
		return label;
	}

	public void loginAction() {
		highlightComponent(accountUser, false);
		highlightComponent(accountPassword, false);
		boolean isFailed = false;
		if (accountUser.getText().length() < 9) {
			highlightComponent(accountUser, true);
			accountUser.requestFocus();
			isFailed = true;
		} else if (accountPassword.getPassword().length == 0) {
			highlightComponent(accountPassword, true);
			accountPassword.requestFocus();
			isFailed = true;
		}
		
		if (!isFailed) {
			if (rememberLogin.isSelected()) {
				configuration.setSetting(host+".account.user", accountUser.getText());
				configuration.setSetting(host+".account.password", scramble(new String(accountPassword.getPassword())));
			}
			
			loginHandler.setSystemUser("mud");
			loginHandler.setSystemPassword(System.getProperty("system.password"));
			loginHandler.setAccountUser(accountUser.getText());
			loginHandler.setAccountPassword(new String(accountPassword.getPassword()));
			loginHandler.login();
			
			gameWindowLayout.doLayout();
		}
	}

	public void guestloginAction() {
		loginHandler.setSystemUser("mudguest");
		loginHandler.login();
		
		gameWindowLayout.doLayout();
	}
	
	private void highlightComponent(JComponent component, boolean isError) {
		if (isError) {
			component.setBackground(new Color(255,0,0));
			component.setForeground(new Color(255,255,255));
		} else {
			component.setBackground(new Color(255,255,255));
			component.setForeground(new Color(0,0,0));
		}
	}

	public void setLoginHandler(LoginFacade loginHandler) {
		this.loginHandler = loginHandler;
	}

	public void setGameWindowLayout(GameWindowLayout gameWindowLayout) {
		this.gameWindowLayout = gameWindowLayout;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

}
