package automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import utils.Triple;

public class NFA extends FA {

	/*
	 *  Construction
	*/
	
	// Constructor
	public NFA(Set<State> states, Set<Character> alphabet, Set<Triple<State,Character,State>> transitions)
	throws IllegalArgumentException
	{
		//Translation of automaton
		this.states = states;
		this.alphabet = alphabet;
		isNFA = true;
		isDFA = isNFALambda = false;
		delta = new HashMap<>();
		for (Triple<State, Character, State> t : transitions) {
			State depState = t.first();
			State arrState = t.third();
			Character alphSymbol = t.second();
			if (!delta.containsKey(depState)) {
				HashMap<Character, Set<State>> snd = new HashMap<>();
				Set<State> arrivalStates = new HashSet<>();
				arrivalStates.add(arrState);
				snd.put(alphSymbol, arrivalStates);
				delta.put(depState, snd);
			}
			else if (!delta.get(depState).containsKey(alphSymbol)) {
				Set<State> arrivalStates = new HashSet<>();
				arrivalStates.add(arrState);
				delta.get(depState).put(alphSymbol,arrivalStates);
			}
			else {
				delta.get(depState).get(alphSymbol).add(arrState);
			}
		}
		for (State s : states) {
			if (!delta.containsKey(s)) {	
				HashMap<Character,Set<State>> snd = new HashMap<>();
				delta.put(s, snd);
			}
		}
		if (!repOk()) 
			throw new IllegalArgumentException();

	}
	
	
	
	/*
	 *  Automata methods
	*/	
	
	
	@Override
	public boolean accepts(String string) {
		assert repOk();
		assert string != null;
		assert verifyString(string);
		Set<State> currentStates = new HashSet<>();
		Set<State> arrStateSet = new HashSet<>();
		currentStates.add(initialState());
		for (int i = 0; i < string.length(); i++) {
			for (State depState : currentStates) {
				//System.out.println(string.charAt(i));
				//System.out.println("delta="+delta(depState,string.charAt(i)));
				for (State arrState : delta(depState,string.charAt(i))) {
					arrStateSet.add(arrState);
				}
			}
			if (arrStateSet.isEmpty())
				return false;
			currentStates.clear();
			for (State arrState : arrStateSet)
				currentStates.add(arrState);
			arrStateSet.clear();
			//System.out.println(currentStates);
		}
		for (State s : currentStates) {
			if (s.isFinal())
				return true;
		}
		return false;
	}
	
	/**
	 * Converts the automaton to a DFA.
	 * 
	 * @return DFA recognizing the same language.	
	*/
	public DFA toDFA() {
		assert repOk();
		Set<Triple<State,Character,State>> transitions = new HashSet<>();
		Queue<Set<State>> currentStatesSet = new LinkedList<>();
		Set<Set<State>> visited = new HashSet<>();
		Set<State> currentStates = new HashSet<>();
		Set<State> arrStateSet = new HashSet<>();
		Set<State> dfaStates = new HashSet<>();
		currentStates.add(initialState());
		currentStatesSet.add(currentStates);
		visited.add(currentStates);
		while(!currentStatesSet.isEmpty()) {
			for (State s : currentStatesSet.remove())
				currentStates.add(s);
			State depSt = toState(currentStates);
			dfaStates.add(depSt);
			for (Character c : alphabet) {
				for (State depState : currentStates) {
					for (State arrState : delta(depState,c)) {
						arrStateSet.add(arrState);
					}
				}
				if (!arrStateSet.isEmpty()) {
					State arrSt = toState(arrStateSet);
					dfaStates.add(arrSt);
					transitions.add(new Triple<>(depSt,c,arrSt));
					if (!visited.contains(arrStateSet)) {
						LinkedList<State> list = new LinkedList<>();
						for (State s : arrStateSet)
							list.add(s);
						currentStatesSet.add(new HashSet<State>(list));
						visited.add(new HashSet<State>(list));
					}
				}
				arrStateSet.clear();
			}
			currentStates.clear();
		}
		
		return new DFA(dfaStates,alphabet,transitions);
	}
	
	private State toState (Set<State> stateSet) {
		//if (stateSet.isEmpty())
		//	throw new IllegalArgumentException("calling toState(set) with an empty set");
		String stateName = "";
		boolean isFinal = false;
		if (stateSet.size()==1 && ((State)stateSet.toArray()[0]).equals(initialState()))
			return new State(initialState().getName(),true,initialState().isFinal());
		for (State s : 	stateSet) {
			stateName+=s.getName();
			if (s.isFinal())
				isFinal = true;
		}
		return new State(stateName,false,isFinal);
	}

	@Override
	public boolean repOk() {
		 return !this.checkLambda() && this.checkInitialStates() && this.checkCorrectTransitions();
	} 


}
