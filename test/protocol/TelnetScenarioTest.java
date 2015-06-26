package protocol;

import io.protocol.TelnetProtocolHandler;
import io.protocol.impl.BasicTelnetProtocolHandler;

import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;
import util.BytesListenerMock;

public class TelnetScenarioTest extends TestCase {
	private TelnetProtocolHandler telnetProtocolHandler;
	private BytesListenerMock bytesListenerMock;
	private ByteArrayOutputStream byteOutputStream;
	
	public void setUp() {
		telnetProtocolHandler
			= new BasicTelnetProtocolHandler();
		
		bytesListenerMock = new BytesListenerMock();
		telnetProtocolHandler.addBytesListener(bytesListenerMock);
		
		byteOutputStream = new ByteArrayOutputStream();
		telnetProtocolHandler.setOutputStream(byteOutputStream);
	}
	
	public void testShouldPassThroughNonTelnetCode() {
		inputBytes(new byte[]{(byte)254, 'a', 'b'});
		expectOnBytes(new byte[]{(byte)254, 'a', 'b'});
		expectNoOutputStreamBytes();
	}

	public void testShouldAllowNonIAC() {
		inputBytes(new byte[]{TelnetProtocolHandler.BYTE_IAC, TelnetProtocolHandler.BYTE_IAC});
		expectOnBytes(new byte[]{TelnetProtocolHandler.BYTE_IAC});
		expectNoOutputStreamBytes();
	}

	public void testShouldWontDoRequests() {
		inputBytes(new byte[]{TelnetProtocolHandler.BYTE_IAC, TelnetProtocolHandler.BYTE_DO, (byte)123});
		expectOnBytes(new byte[]{});
		expectOutputStreamBytes(
			new byte[]{TelnetProtocolHandler.BYTE_IAC, TelnetProtocolHandler.BYTE_WONT, (byte)123});
	}
	
	public void testShouldDontWillRequests() {
		inputBytes(new byte[]{TelnetProtocolHandler.BYTE_IAC, TelnetProtocolHandler.BYTE_WILL, (byte)123});
		expectOnBytes(new byte[]{});
		expectOutputStreamBytes(
			new byte[]{TelnetProtocolHandler.BYTE_IAC, TelnetProtocolHandler.BYTE_DONT, (byte)123});
	}
	
	private void inputBytes(byte[] bytes) {
		telnetProtocolHandler.onBytes(bytes, 0, bytes.length);
	}

	private void expectOutputStreamBytes(byte[] bytes) {
		expectEqual(bytes, byteOutputStream.toByteArray());
	}

	private void expectNoOutputStreamBytes() {
		expectEqual(new byte[]{}, byteOutputStream.toByteArray());
	}
	
	private void expectOnBytes(byte[] bytes) {
		expectEqual(bytes, bytesListenerMock.getBytes());
	}
	
	public void expectEqual(byte[] bytesA, byte[] bytesB) {
		assertEquals(new String(bytesA), new String(bytesB));
	}
}
