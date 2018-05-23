package sensor;

import io.sensor.PlayerListener;
import io.sensor.PlayerSensor;

import java.util.ArrayList;

import junit.framework.TestCase;

public class PlayerSensorTest extends TestCase implements PlayerListener {
	private ArrayList players;
	private PlayerSensor sensor;

	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		sensor = new PlayerSensor();
		sensor.addPlayerSensor(this);
		players = new ArrayList();
	}
	
	public void testShouldDetectQWNovice() {
		
		sensor.onCode("<050006>");
		sensor.onText("Bert");
		sensor.onCode("<>");
		
		assertEquals(1, players.size());
		assertEquals("Bert", players.get(0));
	}

	public void testShouldDetectQWChamp() {
		
		sensor.onCode("<050006>");
		sensor.onText("Alf the champion");
		sensor.onCode("<>");
		
		assertEquals(1, players.size());
		assertEquals("Alf", players.get(0));
	}
	
	public void testShouldDetectQWSirLadyBrotherSister() {
		
		sensor.onCode("<050006>");
		sensor.onText("Sir Alf");
		sensor.onCode("<>");
		sensor.onCode("<050006>");
		sensor.onText("Lady Leslie");
		sensor.onCode("<>");
		sensor.onCode("<050006>");
		sensor.onText("Brother Bert");
		sensor.onCode("<>");
		sensor.onCode("<050006>");
		sensor.onText("Sister Sally");
		sensor.onCode("<>");
		
		assertEquals(4, players.size());
		assertEquals("Alf", players.get(0));
		assertEquals("Leslie", players.get(1));
		assertEquals("Bert", players.get(2));
		assertEquals("Sally", players.get(3));
	}

	public void testShouldDetectInvisVisNovice() {
		
		sensor.onCode("<050005>");
		sensor.onText("Bert fades from view.");
		sensor.onCode("<>");
		sensor.onCode("<050004>");
		sensor.onText("Alf has suddenly become visible");
		sensor.onCode("<>");
		
		assertEquals(2, players.size());
		assertEquals("Bert", players.get(0));
		assertEquals("Alf", players.get(1));
	}
	
	public void testShouldDetectQWInvis() {
		
		sensor.onCode("<050106>");
		sensor.onText("(((Merlyn the arch-wizard)))");
		sensor.onCode("<>");
		
		assertEquals(1, players.size());
		assertEquals("Merlyn", players.get(0));
	}

	public void testShouldDetectLeaveNovice() {
		
		sensor.onCode("<050003>");
		sensor.onText("Bert has just left.");
		sensor.onCode("<>");
		
		assertEquals(1, players.size());
		assertEquals("Bert", players.get(0));
	}
	
	public void testShouldDetectLeaveChamp() {
		
		sensor.onCode("<050003>");
		sensor.onText("Alf the sorcerer has just left.");
		sensor.onCode("<>");
		
		assertEquals(1, players.size());
		assertEquals("Alf", players.get(0));
	}

	public void testShouldDetectLookNovice() {
		
		sensor.onCode("<050001>");
		sensor.onText("Bert is here holding ");
		sensor.onCode("<030100>");
		sensor.onText("a cup of tea");
		sensor.onCode("<>");
		sensor.onText(".");
		sensor.onCode("<>");
		
		assertEquals(1, players.size());
		assertEquals("Bert", players.get(0));
	}
	
	public void onPlayer(String name) {
		players.add(name);
	}
}
