package backend2;

public interface CommandSender {

	void send(String text);

	void send(byte[] bytes);

}
