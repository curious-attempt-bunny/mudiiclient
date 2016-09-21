package gui3;

import gui3.text.ScrollbackController;
import io.listener.StateListener;
import io.sensor.PlayerListener;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JTextField;

import backend2.CommandSender;
import backend2.FunctionKeyStore;
import domain.Configuration;
import domain.State;

public class Prompt implements ComponentWrapper, KeyListener, FontConsumer, PlayerListener, StateListener {

	private static final int COMMAND_HISTORY_LENGTH_MAXIMUM = 10;

	private CommandSender commandSender;

	private State state;

	private List commandHistory;

	private int commandHistoryIndex;

	private ScrollbackController scrollbackController;

	private JTextField component;

//	private ComponentWrapper parent;

	private FontManager fontManager;

	private Configuration configuration;

	private FunctionKeyStore functionKeyStore;
	
	private List players;

	private CommandTransformer commandTransformer;
	
	public void init() {
		players = new Vector();
		commandHistory = new ArrayList();
		commandHistoryIndex = 0;

		component = new JTextField();
		component.setFocusTraversalKeysEnabled(false);
//		component.setFocusCycleRoot(false);
//		component.setFocusTraversalPolicy()
//		FocusTraversalPolicy focusTraversalPolicy;
	
//		Container container = ((JFrame) parent.getComponent()).getContentPane();
//		Container container = (Container) parent.getComponent();
//		container.add(component, BorderLayout.CENTER);
		
		component.addKeyListener(this);
	}

	public void keyPressed(KeyEvent e) {
		if (e.getModifiers() == 0) {
			// no CTRL / ALT / SHIFT etc.

			if (e.getKeyCode() == KeyEvent.VK_UP) {
				onHistoryPrevious();
				e.consume();
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				onHistoryNext();
				e.consume();
			} else if (e.getKeyCode() == KeyEvent.VK_PAGE_UP) {
				scrollbackController.pageUp();
				e.consume();
			} else if (e.getKeyCode() == KeyEvent.VK_PAGE_DOWN) {
				scrollbackController.pageDown();
				e.consume();
			} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if(component.getText().length() < 1) {
					e.consume();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				if (scrollbackController.isEnabled()) {
					scrollbackController.setEnabled(false);
				} else {
					component.setText("");
				}
				e.consume();
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				String cmd = component.getText();
				if (cmd.equals("")
						&& configuration.getInt(Configuration.KEY_ENTER_RESENDS, Configuration.DEFAULT_ENTER_RESENDS) == 1
						&& state.get(State.KEY_PLAYING) == Boolean.TRUE) {
					onHistoryPrevious();
					cmd = component.getText();
				}
				// allows us to expand "op n" via "op n "
				String expanded = commandTransformer.transform(cmd+" ");
				if (!expanded.equals(cmd+" ")) {
					cmd = expanded;
				}
				commandSender.send(cmd + "\r");
				sentCommand(cmd);
				if (configuration.getInt(Configuration.KEY_COMMAND_REMAINS, Configuration.DEFAULT_COMMAND_REMAINS) == 1) {
					component.setSelectionStart(0);
					component.setSelectionEnd(component.getText().length());
 				} else {
 					component.setText("");
 				}
				e.consume();
			} else if (e.getKeyCode() >= KeyEvent.VK_F1 && e.getKeyCode() <= KeyEvent.VK_F9) {
				int fNum = e.getKeyCode() - KeyEvent.VK_F1 + 1;
//				System.out.println("F"+fNum);
				if (functionKeyStore != null) {
					String functionKey = functionKeyStore.getFunctionKey(fNum);
					if (functionKey != null) {
						commandSender.send(functionKey + "\r");
						e.consume();
					}
				}
			} else if (e.getKeyCode() == KeyEvent.VK_TAB) {
				String cmd = component.getText();
				if (cmd.length() > 0) {
					int i = component.getSelectionStart();
					int start = i;
					while(start > 0 && Character.isLetter(cmd.charAt(start-1))) {
						start--;
					}
					int end = start;
					while(end < cmd.length()-1 && Character.isLetter(cmd.charAt(end))) {
						end++;
					}
					if (end < cmd.length()-1 && !Character.isLetter(cmd.charAt(end)) ) {
						end--;
					}
					if (start < i) {
						String key = cmd.substring(start,start+1).toUpperCase()+cmd.substring(start+1,i).toLowerCase();
						String match = null;
						for (Iterator it = players.iterator(); it.hasNext();) {
							String player = (String) it.next();
							
							if (player.startsWith(key)) {
								match = player;
								break;
							}
						}
						
						if (match != null) {
							if (start == 0) {
								match += ' ';
							}
							cmd = cmd.substring(0,start)+match+cmd.substring(end+1);
							component.setText(cmd);
							component.setSelectionStart(end+match.length()-key.length()+1);
							component.setSelectionEnd(component.getSelectionStart());
						}
					}
				}
				e.consume();
			}
		} else if ((e.getModifiers() & KeyEvent.CTRL_MASK) == KeyEvent.CTRL_MASK) {
			char ch;
			if (e.getKeyChar() <= 26) {
				ch = (char) ('A' + e.getKeyChar() - 1);
			} else {
				ch = (char)e.getKeyChar();
			}
			System.out.println("CTRL " + ch);
			
			if (ch == 'P') {
				onHistoryPrevious();
				e.consume();
			} else if (ch == 'N') {
				onHistoryNext();
				e.consume();
			} else if (ch == 'L') {
				onSendPrevious();
				e.consume();
			} else if (ch == 'D') {
				onSendDreamword();
				e.consume();
			} else if (ch == 'X' || ch == 'C' || ch == 'V' || ch == 'A') {
				// clipboard
			} else if (ch == '+' || ch == '=') {
				fontManager.onFontBigger();
				e.consume();
			} else if (e.getKeyCode() == KeyEvent.VK_MINUS) {
				fontManager.onFontSmaller();
				e.consume();
			} else if (ch >= 'A' && ch <= 'Z') {
				// send it to mud
				commandSender.send(new byte[] { (byte) e.getKeyChar() });
				e.consume();
			}
		}
	}

	protected void onSendDreamword() {
		System.out.println("SEND DREAMWORD");
		String dreamword = (String) state.get(State.KEY_DREAMWORD);
		if (dreamword != null) {
			commandSender.send("say " + dreamword + "\r");
			sentCommand(dreamword);
		}
	}

	protected void onSendPrevious() {
		System.out.println("SEND PREV");
		String current = getHistoryCurrent();
		if (current != null) {
			commandSender.send(current + "\r");
			sentCommand(current);
		}
	}

	protected void onHistoryNext() {
		commandHistoryIndex++;
		if (commandHistoryIndex >= commandHistory.size()) {
			commandHistoryIndex = 0;
		}

		highlightCurrent();
	}

	protected void onHistoryPrevious() {
		highlightCurrent();
		
		commandHistoryIndex--;
		if (commandHistoryIndex < 0) {
			commandHistoryIndex = commandHistory.size()-1;
		}
	}

	private void highlightCurrent() {
		component.setText(getHistoryCurrent());
		component.setSelectionStart(0);
		component.setSelectionEnd(getHistoryCurrent().length());
	}

	public void sentCommand(String text) {
		commandHistory.add(text);
		if (commandHistory.size() > COMMAND_HISTORY_LENGTH_MAXIMUM) {
			commandHistory.remove(0);
		}
		commandHistoryIndex = commandHistory.size() - 1;
	}

	private String getHistoryCurrent() {
		if (commandHistoryIndex < commandHistory.size()) {
			return (String) commandHistory.get(commandHistoryIndex);
		} else {
			return "";
		}
	}

	public void setCommandSender(CommandSender commandSender) {
		this.commandSender = commandSender;
	}

	public void keyReleased(KeyEvent e) {
		String cmd = component.getText();
		String transformed = commandTransformer.transform(cmd);
		if (!cmd.equals(transformed)) {
			component.setText(transformed);
		}
	}

	public void setScrollbackController(ScrollbackController scrollbackController) {
		this.scrollbackController = scrollbackController;
	}

	public void setFont(Font font) {
		component.setFont(font);
	}

	public void setFontManager(FontManager fontManager) {
		this.fontManager = fontManager;
	}

	public Component getComponent() {
		return component;
	}

//	public void setParent(ComponentWrapper parent) {
//		this.parent = parent;
//	}

	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setState(State state) {
		this.state = state;
	}

	public void setFunctionKeyStore(FunctionKeyStore functionKeyStore) {
		this.functionKeyStore = functionKeyStore;
	}

	public void onPlayer(String name) {
		players.add(name);
		Collections.sort(players);
	}

	public void onState(String key, Object value) {
		if (key == State.KEY_RESET_NUMBER) {
			players.clear();
		}
	}

	public void setCommandTransformer(CommandTransformer commandTransformer) {
		this.commandTransformer = commandTransformer;
	}
}
