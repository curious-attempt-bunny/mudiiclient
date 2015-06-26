package backend2;

import io.protocol.impl.BasicMudClientFilter;
import io.protocol.impl.BasicTelnetProtocolHandler;
import io.trigger.Trigger;
import io.trigger.TriggerAction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class RobustInputOutput implements InputOutput {

	private List outputListeners;
	private String host;
	private BasicMudClientFilter mudClientFilter;
	private BasicTelnetProtocolHandler telnetProtocolHandler;
	private Socket socket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private boolean isConnected;
	private List commands;

	public RobustInputOutput() {
		outputListeners = new Vector();
		commands = new ArrayList();
	}
	
	public void addOutputListener(OutputListener outputListener) {
		outputListeners.add(outputListener);
	}

	public void connect() {
		new Thread(new Runnable() {
			public void run() {
				runConnect();
			}
		}).start();
	}

	protected void runConnect() {
		isConnected = false;
		fireOutput("connecting...\r");
		
		try {
			socket = new Socket(host, 23);
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();
			isConnected = true;
		} catch (IOException e) {
			fireOutput("connected failed: "+e.getMessage());
		}
		
		if (isConnected) {
			telnetProtocolHandler.setOutputStream(outputStream);
			
			new Thread(new Runnable() {
				public void run() {
					runRead();
				}
			}).start();
			
			runWrite();
		}
	}

	private void fireOutputEnd() {
		Iterator it = outputListeners.iterator();
		while (it.hasNext()) {
			OutputListener outputListener = (OutputListener) it.next();
			try {
				outputListener.onOutputEnd();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void fireOutputStart() {
		Iterator it = outputListeners.iterator();
		while (it.hasNext()) {
			OutputListener outputListener = (OutputListener) it.next();
			try {
				outputListener.onOutputStart();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void fireOutput(String text) {
		Iterator it = outputListeners.iterator();
		while (it.hasNext()) {
			OutputListener outputListener = (OutputListener) it.next();
			try {
				outputListener.onOutput(text);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	protected void runWrite() {
		synchronized (commands) {
		while(true) {
			if (commands.isEmpty()) {
				try {
					commands.wait();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
	//				e.printStackTrace();
				}
			}
			byte[] command = null;
				if (!commands.isEmpty()) {
					command = (byte[]) commands.remove(0);
				}	
			if (command != null) {
				try {
					outputStream.write(command);
					outputStream.flush();
				} catch (Exception e) {
					//onIOExcepion(e);
					e.printStackTrace();
				}	
			}
		}
		}
	}

	protected void runRead() {
		try {
			byte[] buffer = new byte[4096];
			while (true) {
				int read = inputStream.read(buffer);
				if (read > 0) {
					fireOutputStart();
					try {
						telnetProtocolHandler.onBytes(buffer, 0,
								read);
					} catch (Exception e) {
						e.printStackTrace();
					}
					fireOutputEnd();
				}
			}
		} catch (final IOException e) {
//			onIOExcepion(e);
			e.printStackTrace();
		}
	}

	public void init() {
		// TODO Auto-generated method stub
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setMudClientFilter(BasicMudClientFilter mudClientFilter) {
		this.mudClientFilter = mudClientFilter;
	}

	public void setTelnetProtocolHandler(
			BasicTelnetProtocolHandler telnetProtocolHandler) {
				this.telnetProtocolHandler = telnetProtocolHandler;
	}

	public void onText(String text) {
		fireOutput(text);
	}

	public void send(String string) {
		send(string.getBytes());
	}

	public void send(byte[] bytes) {
//		if (outputStream == null) {
//			//onDisconnected();
//		} else {
//			try {
//				outputStream.write(bytes);
//				outputStream.flush();
//			} catch (Exception e) {
//				//onIOExcepion(e);
//			}
//		}
		synchronized (commands) {
			commands.add(bytes);
			commands.notifyAll();
		}
	}


	public void onCode(String code) {
		fireOutput(code);
	}

	public void addTrigger(String trigger, String text) {
		addTrigger(trigger, text.getBytes());
	}

	public void addTrigger(String trigger, final byte[] bs) {
		mudClientFilter.addTextListener(new Trigger(trigger, new TriggerAction() {
			public void onTrigger() {
				send(bs);
			}
		}));
	}

}
