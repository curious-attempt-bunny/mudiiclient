package backend2;

public interface TriggerHandler {
	void addTrigger(String trigger, String text);
	void addTrigger(String trigger, final byte[] bs);
}
