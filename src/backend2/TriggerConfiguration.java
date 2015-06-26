package backend2;

import java.io.BufferedReader;
import java.io.FileReader;

public class TriggerConfiguration {

	private TriggerHandler triggerHandler;

	private String fileName;

	public void setFile(String fileName) {
		this.fileName = fileName;
	}

	public void setTriggerHandler(TriggerHandler triggerHandler) {
		this.triggerHandler = triggerHandler;
	}

	public void init() {
		if (fileName != null) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(
						fileName));

				while (true) {
					String line = reader.readLine();

					if (line == null) {
						break;
					}

					if (line.indexOf("=") != -1) {
						String trigger = line.substring(0, line.indexOf("="))
								.trim();
						String text = line.substring(line.indexOf("=") + 1)
								.trim();

						triggerHandler.addTrigger(".*" + trigger + ".*", text
								+ "\r");
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
