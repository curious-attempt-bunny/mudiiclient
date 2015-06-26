package backend2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import domain.State;

public class CommandHistory implements CommandSender, Runnable, Comparator {
	public class Command {
		public String command;
		String where;
		public int frequency;
		private boolean isBonus;
		private boolean isPenalty;
		
		Command(String command, String where, int frequency) {
			this.command = command;
			this.where = where;
			this.frequency = frequency;
			
			if (command.indexOf(',') != -1 || command.indexOf('.') != -1 ) {
				isBonus = true;
			} else if ("n|ne|e|se|s|sw|w|nw|u|d|up|down|in|out|o|zw".indexOf(command+"|") != -1) {
				isPenalty = true;
			}
		}

		public int getWeightedFrequency() {
			return (isPenalty ? (int)Math.ceil(frequency / 10) : (isBonus ? 2*frequency + 1 : frequency));
		}
		
		public boolean equals(Object obj) {
			return ((Command)obj).command.equals(command);
		}
	}
	
	private static final String FILENAME = "history.txt";
	private CommandSender commandSender;
//	private Configuration configuration;
	private State state;
	private List commands;
	private Map mapWhereToCommands;
	
	public CommandHistory() {
		commands = new ArrayList();
		mapWhereToCommands = new HashMap();
	}
	
	public void init() {
		new Thread(this).start();
	}
	
//	public void setConfiguration(Configuration configuration) {
//		this.configuration = configuration;
//	}

	public void setCommandSender(CommandSender commandSender) {
		this.commandSender = commandSender;
	}

	public void send(String text) {
		commandSender.send(text);
		try {
			if (Boolean.TRUE.equals(state.get(State.KEY_PLAYING))) {
				String cmd = text;
				if (cmd.endsWith("\r")) {
					cmd = cmd.substring(0,cmd.length()-1); 
				}
				add(new Command(cmd, (String) state.get(State.KEY_ROOM_SHORT_NAME), 1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void add(Command command) {
		synchronized (commands) {
			commands.add(command);
			incrementFrequency(command);
			commands.notifyAll();
		}
	}

	public void send(byte[] bytes) {
		commandSender.send(bytes);
	}

	public void setState(State state) {
		this.state = state;
	}

	public void run() {
		readCommands();
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
			
			List items = new ArrayList();
			while(true) {
				synchronized(commands) {
					if (commands.isEmpty()) {
						try {
							commands.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
//							e.printStackTrace();
						}
					}
					items.addAll(commands);
					commands.clear();
				}
				try {
					Iterator it = items.iterator();
					while(it.hasNext()) {
						Command item = (Command)it.next();
						bufferedWriter.write(item.where);
						bufferedWriter.write(';');
						bufferedWriter.write(item.command);
						bufferedWriter.write(';');
						bufferedWriter.write(Integer.toString(item.frequency));
						bufferedWriter.write("\n");
					}
					bufferedWriter.flush();
				} catch (IOException e) {
					e.printStackTrace();
//					break;
				}
				items.clear();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readCommands() {
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(FILENAME));
			
			while(true) {
				String line = bufferedReader.readLine();
				if (line == null) {
					break;
				}
				int pos1 = line.indexOf(';');
				if (pos1 == -1) {
					continue;
				}
				int pos2 = line.lastIndexOf(';');
				if (pos2 == -1 && pos1 != pos2) {
					continue;
				}
//				int pos3 = line.indexOf(';', pos2);
//				System.out.println(pos1+" "+pos2+" "+line);
				String where = line.substring(0, pos1);
				String cmd = line.substring(pos1+1, pos2).replaceAll("\\n", "\r");
				int freq = Integer.parseInt(line.substring(pos2+1));
				
				boolean isUsable = true;
				if (cmd.indexOf("\"") != -1
						|| (cmd.length() == 2 && cmd.charAt(0) == 'q')
						|| (cmd.startsWith("re "))
						|| (cmd.startsWith("say "))
						|| cmd.indexOf("'") != -1) {
					isUsable = false;
				} else if(cmd.length() > 10) {
					int count = 0;
					for(int i=0; i<cmd.length(); i++) {
						if (cmd.charAt(i) == ' ') {
							count++;
						}
					}
					
					if (count > 7) {
						isUsable = false;
					}
				}
				
				if (isUsable) {
					synchronized (commands) {
						incrementFrequency(new Command(cmd, where, freq));
						commands.notifyAll();
					}
				}
			}
		} catch (FileNotFoundException e) {
			// suppressed
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    private void incrementFrequency(Command command) {
		Map commands = (Map) mapWhereToCommands.get(command.where);
		
		if (commands == null) {
			commands = new HashMap();
			mapWhereToCommands.put(command.where, commands);
		}
		
		Command existing = (Command) commands.get(command.command);
		if (existing != null) {
			existing.frequency += command.frequency;
		} else {
			commands.put(command.command, command);
		}
	}

	public List getBest(String where) {
		Map commands = (Map) mapWhereToCommands.get(where);
		List best = new ArrayList();
		
		if (commands != null) {
			best.addAll(commands.values());
			Collections.sort(best, this);
		}
		
		return best;
	}

	public int compare(Object arg0, Object arg1) {
		return new Integer(((Command)arg1).getWeightedFrequency()).compareTo(new Integer(((Command)arg0).getWeightedFrequency()));
	}
}
