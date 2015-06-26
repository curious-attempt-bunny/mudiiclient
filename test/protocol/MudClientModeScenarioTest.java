package protocol;

import io.listener.CodeListener;
import io.listener.TextListener;
import io.protocol.MudClientProtocolHandler;
import io.protocol.impl.BetterMudClientProtocolHandler;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class MudClientModeScenarioTest extends TestCase implements TextListener, CodeListener {
	private MudClientProtocolHandler mudClientProtocolHandler;
	private List events;
	private List texts;
	private List codes;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		mudClientProtocolHandler = new BetterMudClientProtocolHandler();
		mudClientProtocolHandler.addCodeListener(this);
		mudClientProtocolHandler.addTextListener(this);
		events = new ArrayList();
		texts = new ArrayList();
		codes = new ArrayList();
	}
	
	public void testShouldPassThroughText() {
		inputBytes("just a string".getBytes());
		
		assertEquals(1, events.size());
		assertEquals("just a string", events.get(0));
	}

//	public void testShouldPassThroughPoundSign() {
//		inputBytes(new byte[] { (byte)'£'});
//		
//		assertEquals(1, events.size());
//		assertEquals("£", events.get(0));
//	}
	
	public void testShouldHandleSimplestCode() {
		inputBytes(new byte[]{(byte)255});
		
		assertEquals(1, events.size());
		assertEquals("<>", events.get(0));
	}
	
	public void testShouldHandleChainedSimpleCode() {
		inputBytes(new byte[]{(byte)255, (byte)255});
		
		assertEquals(2, events.size());
		assertEquals("<>", events.get(0));
		assertEquals("<>", events.get(1));
	}
	
	public void testShouldHandleCode() {
		inputBytes(new byte[]{(byte)155, (byte)255});
		
		assertEquals(1, events.size());
		assertEquals("<00>", events.get(0));
	}
	
	public void testShouldHandleSplitCode() {
		inputBytes(new byte[]{(byte)155});
		inputBytes(new byte[]{(byte)255});
		assertEquals(1, events.size());
		assertEquals("<00>", events.get(0));
	}
	
	public void testShouldStripOddNewlines() {
		inputBytes(new byte[]{(byte)'t', (byte)13, (byte)13, (byte)10});
		
		assertEquals(1, events.size());
		assertEquals(new String(new byte[]{(byte)'t', (byte)13, (byte)10}), events.get(0));
	}
	
	private void inputBytes(byte[] bytes) {
		mudClientProtocolHandler.onBytes(bytes, 0, bytes.length);
	}

	public void onText(String text) {
		events.add(text);
		texts.add(text);
	}

	public void onCode(String code) {
		events.add(code);
		codes.add(code);
	}

	
}
