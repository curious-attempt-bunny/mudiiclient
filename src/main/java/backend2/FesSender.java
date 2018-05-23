package backend2;

import io.listener.StateListener;

import java.util.Date;

import domain.Configuration;
import domain.State;

public class FesSender implements CommandSender, StateListener {
	public static final String FES = "\u001b-[fes\u001b-]";
	private CommandSender commandSender;
	private long timeLastSentFes = 0;
	private boolean isFesEnable;
	private Configuration configuration;
	
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	public void setCommandSender(CommandSender commandSender) {
		this.commandSender = commandSender;
	}

	public void send(String text) {
		commandSender.send(text);
		if (isFesEnable && new Date().getTime() - timeLastSentFes > 5000) {
			checkSendFes();
		}
	}

	public void send(byte[] bytes) {
		commandSender.send(bytes);
		if (isFesEnable && new Date().getTime() - timeLastSentFes > 5000) {
			checkSendFes();
		}
	}

	public void onState(String key, Object value) {
		if (key == State.KEY_PLAYING) {
			isFesEnable = ((Boolean)value).booleanValue();
			checkSendFes();
		}
	}

	private void checkSendFes() {
		if (isFesEnable && configuration.getInt(Configuration.KEY_ACTIVE_DATA_COLLECTION, Configuration.DEFAULT_ACTIVE_DATA_COLLECTION) == 1) {
			commandSender.send(FES);
			timeLastSentFes = new Date().getTime();
		}
	}

}
