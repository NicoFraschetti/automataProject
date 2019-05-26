package automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import utils.Triple;

public class NFALambda extends FA {
	
	/*
	 *  Construction
	*/
	
	// Constructor
	public NFALambda(Set<State> states,	Set<Character> alphabet, 
			Set<Triple<State,Character,State>> transitions) throws IllegalArgumentException
	{
		//Translation of automaton
		this.states = states;
		this.alphabet = alphabet;
		this.alphabet.add(Lambda);
		isNFALambda = true;
		isDFA = isNFA = false;
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
		currentStates = lambdaClosure(currentStates);
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
			currentStates = lambdaClosure(arrStateSet);
			arrStateSet.clear();
			//System.out.println(currentStates);
		}
		for (State s : currentStates) {
			if (s.isFinal())
				return true;
		}
		return false;
	}
	
	private Set<State> lambdaClosure(Set<State> stateSet) {
		Set<State> res = new HashSet<>();
		Queue<State> statesQueue = new LinkedList<>();
		Set<State> visited = new HashSet<>();
		for (State s : stateSet) {
			statesQueue.add(s);
			visited.add(s);
		}
		while (!statesQueue.isEmpty()) {
			State depState = statesQueue.remove();
			res.add(depState);
			for (State arrState : delta(depState,Lambda)) {
				if (!visited.contains(arrState)) {
					statesQueue.add(arrState);
					visited.add(arrState);
				}
			}
		}
		return res;
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
		//System.out.println("initialState="+initialState());
		currentStates = lambdaClosure(currentStates);
		//System.out.println("currentStates="+currentStates);
		LinkedList<State> initialList = new LinkedList<>();
		for (State s : arrStateSet)
			initialList.add(s);
		currentStatesSet.add(new HashSet<State>(initialList));
		visited.add(new HashSet<State>(initialList));
		Set<Character> newAlphabet = new HashSet<>();
		for (Character c : alphabet) {
			if (!c.equals(Lambda))
				newAlphabet.add(c);
		}
		while(!currentStatesSet.isEmpty()) {
			for (State s : currentStatesSet.remove())
				currentStates.add(s);
			State depSt = toState(currentStates);
			dfaStates.add(depSt);
			for (Character c : newAlphabet) {
				for (State depState : currentStates) {
					for (State arrState : delta(depState,c)) {
						arrStateSet.add(arrState);
					}
				}
				arrStateSet = lambdaClosure(arrStateSet);
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
		return new DFA(dfaStates,newAlphabet,transitions);
	}
	
	private State toState (Set<State> stateSet) {
		//if (stateSet.isEmpty())
		//	throw new IllegalArgumentException("calling toState(set) with an empty set");
		String stateName = "";
		boolean isInitial = false;
		boolean isFinal = false;
		for (State s : 	stateSet) {
			stateName+=s.getName();
			if (s.isFinal())
				isFinal = true;
			if(s.isInitial())
				isInitial= true;
		}
		return new State(stateName,isInitial,isFinal);
	}
	
	@Override
	public boolean repOk() {
		return this.checkInitialStates() && this.checkCorrectTransitions() && this.checkLambda(); 

	}

}
