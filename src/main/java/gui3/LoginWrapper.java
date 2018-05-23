package gui3;

import gui3.layout.GameWindowLayout;
import gui3.login.LoginFacade;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import backend2.BareBonesBrowserLaunch;
import domain.Configuration;

public class LoginWrapper implements ComponentWrapper {
	private Color fg = new Color(0xff,0xbb, 0x3f);
	private Color bg = new Color(0,0,0x4e);
	private JPanel component;
	private LoginFacade loginHandler;
	private JComboBox systemUser;
	private JPasswordField systemPassword;
	private JTextField accountUser;
	private JPasswordField accountPassword;
	private JCheckBox rememberLogin;
	private GameWindowLayout gameWindowLayout;
	private Configuration configuration;
	private String host;
	private JLabel systemPasswordLabel;
	private JLabel accountUserLabel;
	private JLabel accountPasswordLabel;

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
		component.add(createLabel("System user:"), c);

		c.gridx=4;
		c.gridy=y++;
		systemUser = new JComboBox(new String[] {"mud","mudguest"});
		systemUser.setEditable(true);
		JTextField editor = (JTextField) systemUser.getEditor().getEditorComponent();
		editor.setColumns(10);
		component.add(systemUser, c);
		systemUser.getEditor().getEditorComponent().addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
			}

			public void keyPressed(KeyEvent e) {
				updateEnabledState();
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					loginAction();
				}

			}

			public void keyReleased(KeyEvent e) {

			}
		});
		systemUser.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateEnabledState();
			}
		});
//		systemUser.addActionListener(loginActionListener);


		c.gridx=3;
		c.gridy=y;
		systemPasswordLabel = createLabel("System password:");
		component.add(systemPasswordLabel, c);

		c.gridx=4;
		c.gridy=y++;
		systemPassword = new JPasswordField(12);
		component.add(systemPassword, c);
		systemPassword.addActionListener(loginActionListener);

		c.gridx=3;
		c.gridy=y;
		accountUserLabel = createLabel("Account ID:");
		component.add(accountUserLabel, c);
		
		c.gridx=4;
		c.gridy=y++;
		accountUser = new JTextField(12);
		component.add(accountUser, c);
		accountUser.addActionListener(loginActionListener);
		
		c.gridx=3;
		c.gridy=y;
		accountPasswordLabel = createLabel("Password:");
		component.add(accountPasswordLabel, c);
		
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

		c.gridx=4;
		c.gridy=y++;
		c.anchor=GridBagConstraints.EAST;
		JButton loginButton = createButton("Login");
		component.add(loginButton, c);
		loginButton.addActionListener(loginActionListener);
		
		if (configuration.getInt(host+".login.remember", 1) == 1) {
			rememberLogin.setSelected(true);
			systemUser.setSelectedItem(configuration.getSetting(host + ".system.user", "mud"));
			systemPassword.setText(unscramble(configuration.getSetting(host + ".system.password", "")));
			accountUser.setText(configuration.getSetting(host+".account.user", ""));
			accountPassword.setText(unscramble(configuration.getSetting(host+".account.password", "")));
		}

		highlightComponent(systemUser, false);
		highlightComponent(systemPassword, false);
		highlightComponent(accountUser, false);
		highlightComponent(accountPassword, false);

//		component.addPropertyChangeListener();

		component.addAncestorListener(new AncestorListener() {
			public void ancestorAdded(AncestorEvent event) {
				accountUser.requestFocus();
			}

			public void ancestorRemoved(AncestorEvent event) {

			}

			public void ancestorMoved(AncestorEvent event) {

			}
		});

		updateEnabledState();
	}

	private void updateEnabledState() {
		String sysUser = getSystemUser();
		boolean enabled = !(sysUser.equals("mud") || sysUser.equals("mudguest"));
		systemPassword.setEnabled(enabled);
		systemPasswordLabel.setEnabled(enabled);
		if (!enabled) {
			highlightComponent(systemPassword, false);
		}

		enabled = !sysUser.equals("mudguest");
		accountUserLabel.setEnabled(enabled);
		accountUser.setEnabled(enabled);
		accountPasswordLabel.setEnabled(enabled);
		accountPassword.setEnabled(enabled);
		if (!enabled) {
			highlightComponent(accountUser, false);
			highlightComponent(accountPassword, false);
		}
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
		JCheckBox checkBox = new JCheckBox("remember login details");
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
		highlightComponent(systemUser, false);
		highlightComponent(systemPassword, false);
		highlightComponent(accountUser, false);
		highlightComponent(accountPassword, false);
		boolean isFailed = false;

		String sysUser = getSystemUser();

		if (!sysUser.equals("mudguest") && accountUser.getText().length() < 9) {
			highlightComponent(accountUser, true);
			accountUser.requestFocus();
			isFailed = true;
		} else if (!sysUser.equals("mudguest") && accountPassword.getPassword().length == 0) {
			highlightComponent(accountPassword, true);
			accountPassword.requestFocus();
			isFailed = true;
		} else if (systemPassword.getPassword().length == 0 && !(sysUser.equals("mud") || sysUser.equals("mudguest"))) {
			highlightComponent(systemPassword, true);
			systemPassword.requestFocus();
			isFailed = true;
		}
		
		if (!isFailed) {
			if (rememberLogin.isSelected()) {
				configuration.setSetting(host+".system.user", sysUser);
				configuration.setSetting(host+".system.password", scramble(new String(systemPassword.getPassword())));
				configuration.setSetting(host+".account.user", accountUser.getText());
				configuration.setSetting(host+".account.password", scramble(new String(accountPassword.getPassword())));
			}
			
			loginHandler.setSystemUser(sysUser);
			loginHandler.setSystemPassword(new String(systemPassword.getPassword()));
			loginHandler.setAccountUser(accountUser.getText());
			loginHandler.setAccountPassword(new String(accountPassword.getPassword()));
			loginHandler.login();
			
			gameWindowLayout.doLayout();
		}
	}

	private String getSystemUser() {
		String sysUser;
		if (systemUser.isValid()) {
			sysUser = (String) systemUser.getSelectedItem();
		} else {
			sysUser = (String) systemUser.getEditor().getItem();
		}
		return sysUser;
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
