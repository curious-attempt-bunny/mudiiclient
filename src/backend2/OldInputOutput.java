package backend2;

import io.protocol.impl.BasicMudClientFilter;
import io.protocol.impl.BasicTelnetProtocolHandler;
import io.trigger.Trigger;
import io.trigger.TriggerAction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class OldInputOutput implements Runnable, InputOutput {

	private String host;
	private Socket socket;
	private OutputStream outputStream;
	private InputStream inputStream;
	private BasicTelnetProtocolHandler telnetProtocolHandler;
	private List outputListeners;
	private BasicMudClientFilter mudClientFilter;
	
	public OldInputOutput() {
		outputListeners = new Vector();
	}
	
	/* (non-Javadoc)
	 * @see backend2.InputOutput#setHost(java.lang.String)
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/* (non-Javadoc)
	 * @see backend2.InputOutput#init()
	 */
	public void init() {
//		new Thread(this).start();
	}

	public void run() {
		try {
			fireOutput("connecting...\r");
			
			socket = new Socket(host, 23);
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();
			telnetProtocolHandler.setOutputStream(outputStream);

			byte[] buffer = new byte[4096];
			while (true) {
				int read = inputStream.read(buffer);
				if (read > 0) {
					fireOutputStart();
//					System.out.print(new String(buffer, 0, read));
					telnetProtocolHandler.onBytes(buffer, 0,
							read);
//					for(int i = 0; i<read; i++) {
//						telnetProtocolHandler.onBytes(buffer, i, 1);
//					}
					fireOutputEnd();
				}
			}
		} catch (final IOException e) {
			onIOExcepion(e);
		}
	}

	private void onIOExcepion(Exception e) {
		onDisconnected();
		System.err.println("Disconnected, trace is:");
		e.printStackTrace();
	}

	private void onDisconnected() {
		outputStream = null;
		fireOutput("Disconnected\r");
	}

	private void fireOutputEnd() {
		Iterator it = outputListeners.iterator();
		while (it.hasNext()) {
			OutputListener outputListener = (OutputListener) it.next();
			outputListener.onOutputEnd();
		}
	}

	private void fireOutputStart() {
		Iterator it = outputListeners.iterator();
		while (it.hasNext()) {
			OutputListener outputListener = (OutputListener) it.next();
			outputListener.onOutputStart();
		}
	}

	private void fireOutput(String text) {
		Iterator it = outputListeners.iterator();
		while (it.hasNext()) {
			OutputListener outputListener = (OutputListener) it.next();
			outputListener.onOutput(text);
		}
	}

	/* (non-Javadoc)
	 * @see backend2.InputOutput#setTelnetProtocolHandler(io.protocol.impl.BasicTelnetProtocolHandler)
	 */
	public void setTelnetProtocolHandler(BasicTelnetProtocolHandler telnetProtocolHandler) {
		this.telnetProtocolHandler = telnetProtocolHandler;
	}

	/* (non-Javadoc)
	 * @see backend2.InputOutput#addOutputListener(backend2.OutputListener)
	 */
	public void addOutputListener(OutputListener outputListener) {
		outputListeners.add(outputListener);
	}

	public void onText(String text) {
//		synchronized (this) {
//			System.out.print(text);
			fireOutput(text);
//		}
	}

	public void send(String string) {
		send(string.getBytes());
	}

	public synchronized void send(byte[] bytes) {
		if (outputStream == null) {
			onDisconnected();
		} else {
//			System.out.println("sending "+new String(bytes));
			try {
				outputStream.write(bytes);
				outputStream.flush();
			} catch (Exception e) {
				onIOExcepion(e);
			}
//			System.out.println("done sending "+new String(bytes));
		}
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

	/* (non-Javadoc)
	 * @see backend2.InputOutput#setMudClientFilter(io.protocol.impl.BasicMudClientFilter)
	 */
	public void setMudClientFilter(BasicMudClientFilter mudClientFilter) {
		this.mudClientFilter = mudClientFilter;
	}

	public void onCode(String code) {
//		synchronized (this) {
//			System.out.print(code);
			fireOutput(code);
//		}
	}
	
	/* (non-Javadoc)
	 * @see backend2.InputOutput#connect()
	 */
	public void connect() {
		new Thread(this).start();
	}
}
