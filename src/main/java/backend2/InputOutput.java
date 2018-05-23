package backend2;

import io.listener.CodeListener;
import io.listener.TextListener;
import io.protocol.impl.BasicMudClientFilter;
import io.protocol.impl.BasicTelnetProtocolHandler;

public interface InputOutput extends TextListener, CommandSender, CodeListener, TriggerHandler {

	public abstract void setHost(String host);

	public abstract void init();

	public abstract void setTelnetProtocolHandler(
			BasicTelnetProtocolHandler telnetProtocolHandler);

	public abstract void addOutputListener(OutputListener outputListener);

	public abstract void setMudClientFilter(BasicMudClientFilter mudClientFilter);

	public abstract void connect();

}