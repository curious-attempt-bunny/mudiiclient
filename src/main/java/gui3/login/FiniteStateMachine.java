package gui3.login;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class FiniteStateMachine {
	private Map mapStateToTransitions;
	
	public FiniteStateMachine() {
		mapStateToTransitions = new HashMap();
	}
	
	public void addTransition(String stateFrom, StateTransition stateTransition) { //String pattern, String response, String stateTo) {
		List triggers = (List) mapStateToTransitions.get(stateFrom);
		if (triggers == null) {
			triggers = new Vector();
			mapStateToTransitions.put(stateFrom, triggers);
		}
		triggers.add(stateTransition);
	}

	public StateTransition findMatchingTransition(String state, String triggerText) {
		StateTransition match = null;
		List triggers = (List) mapStateToTransitions.get(state);
		if (triggers != null) {
			
			for (Iterator it = triggers.iterator(); it.hasNext();) {
				StateTransition stateTransition = (StateTransition) it.next();
				
				if (stateTransition.isMatch(triggerText)) {
					match = stateTransition;
					break;
				}
			}
		}
		return match;
	}

}
