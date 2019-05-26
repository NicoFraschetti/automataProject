package automata;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import utils.Triple;

/* Implements a DFA (Deterministic Finite Atomaton).
*/
public class DFA extends FA {

	/*	
	 * 	Construction
	*/
	
	public DFA(Set<State> states, Set<Character> alphabet, 
			Set<Triple<State,Character,State>> transitions) throws IllegalArgumentException 
	{	
		//Translation of automaton
		this.states = states;
		this.alphabet = alphabet;
		isDFA = true;
		isNFA = isNFALambda = false;
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


	public boolean isComplete() {
		for (State s : states) {
			if (!delta.containsKey(s))
				return false;
			for (Character c : alphabet) {
				if (!delta.get(s).containsKey(c))
					return false;
			}
		}
		return true;
	}
	
	public void completeDFA() {
		if (!isComplete()) {
			State errHandlerState = new State("errorHandler", false, false);
			states.add(errHandlerState);
			HashMap<Character, Set<State>> snd = new HashMap<>();
			delta.put(errHandlerState, snd);
			//for (State s : states) {
			//	if (!delta.containsKey(s)) {	
			//		snd = new HashMap<>();
			//		delta.put(s, snd);
			//	}
			//		
			//}
			for (State s : delta.keySet()) {
				for (Character c : alphabet) {
					if (!delta.get(s).containsKey(c)) {
						Set<State> arrivalStates = new HashSet<>();
						arrivalStates.add(errHandlerState);
						delta.get(s).put(c, arrivalStates);
					}
				}
			}
		}
	}
	
	/*
	*	State querying 
	*/
	
	
	
	/*
	 *  Automata methods
	*/
	
	
	@Override
	public boolean accepts(String string) {
		assert repOk();
		assert string != null;
		assert verifyString(string);
		State currentState = initialState();
		for (int i = 0; i < string.length(); i++) {
			Set<State> currStateSet = delta(currentState, string.charAt(i));
			if (currStateSet.isEmpty())
				return false;
			else
				currentState = (State) currStateSet.toArray()[0];
		}
		return currentState.isFinal();
	}

	/**
	 * Converts the automaton to a NFA.
	 * 
	 * @return NFA recognizing the same language.
	 */
	public NFA toNFA() {
		assert repOk();
		return new NFA(states,alphabet,getTransitions());
	}
	
	/**
	 * Converts the automaton to a NFALambda.
	 * 
	 * @return NFALambda recognizing the same language.
	 */
	public NFALambda toNFALambda() {
		assert repOk();
		return new NFALambda(states, alphabet, getTransitions());
	}

	/**
	 * Checks the automaton for language emptiness.
	 * 
	 * @returns True iff the automaton's language is empty.
	 */
	public boolean isEmpty() {
		assert repOk();
	    State initialState = this.initialState();
	    Set<State> visited = new HashSet<>();
	    Queue<State> queue = new LinkedList<>();
	    visited.add(initialState);
	    queue.add(initialState);
	    while (!queue.isEmpty()) {
	    	State currentState = queue.remove();
	    	if (currentState.isFinal())
    			return false;
	    	for (Character c : alphabet) {
		    	for (State s : delta.get(currentState).get(c)) {
		    		visited.add(s);
		    		queue.add(s);
		    	}
		    }
	    }
		return true;		
	}

	/**
	 * Checks the automaton for language infinity.
	 * 
	 * @returns True iff the automaton's language is finite.
	 */
	public boolean isFinite() {
		Set<State> finals = this.finalStates();
		for (State s : finals) {
			for (Character c : alphabet) {
			 if (delta(s, c).equals(s))
				 return false;
			}
		}
		Boolean found = false;
		State init = this.initialState();
		Queue<State> statesQueue = new LinkedList<>();
		Set<State> visited = new HashSet<>();
		statesQueue.add(init);
		visited.add(init);
		while (!statesQueue.isEmpty()) {
			State depState = statesQueue.remove();
			for (Character c : alphabet) {
				for (State arrState : delta(depState,c)) {
					if (visited.contains(arrState)) { 
						found = true;
					} else {
						visited.add(arrState);
						statesQueue.add(arrState);
					}
					if (found) {
						Queue<State> statesQueue2 = new LinkedList<>();
						Set<State> visited2 = new HashSet<>();
						statesQueue2.add(arrState);
						visited2.add(arrState);
						while (!statesQueue2.isEmpty()) {
							State depState2 = statesQueue2.remove();
								for (Character c2 : alphabet) {
									for(State arrState2 : delta(depState2,c2)) {
										if (arrState2.isFinal())
											return false;
										if (!visited2.contains(arrState2))
											statesQueue.add(arrState2);
											visited2.add(arrState2);
									}	
								}	
						}
						found = false;
					}					
				}
			}			
		}
		return true;
	}
	

	
	/**
	 * Returns a new automaton which recognizes the complementary
	 * language. 
	 * 
	 * @returns a new DFA accepting the language's complement.
	 */
	public DFA complement() {
		assert repOk();
		completeDFA();
		Set<State> complementDFAStates = new HashSet<>();
		Set<Triple<State, Character, State>> transitions = new HashSet<>();
		for (State s : states) {
			State complState = s.complement();
			for(Character c : delta.get(s).keySet())
				transitions.add(new Triple<State, Character, State>(complState, c, ((State) delta(s,c).toArray()[0]).complement()));
			complementDFAStates.add(complState);
		}
		return new DFA(complementDFAStates, alphabet, transitions);		
	}
	
	/**
	 * Returns a new automaton which recognizes the kleene closure
	 * of language. 
	 * 
	 * @returns a new DFA accepting labguajes's closure.
	 */
	public DFA star() {
	
		assert repOk();
		
		Set<Triple<State, Character, State>> tSet = new HashSet<>();
		Set<State> sSet = new HashSet<>();
		State thisInitial = this.initialState();
		Set<State> finalStates = this.finalStates();
		
		Integer n = this.states.size()+1;
		String g = n.toString();
		State newOrigin = new State("q"+g, true, true);
		sSet.add(newOrigin);
		
		for(Triple<State, Character, State> t : this.getTransitions()) {
			tSet.add(t);
		}
		
		for (Character c : delta.get(thisInitial).keySet()) {
			Triple<State, Character, State> t = new Triple<>(newOrigin, c, (State) delta.get(thisInitial).get(c).toArray()[0]);
			tSet.add(t);
		}
		
		for (State s : finalStates) {
			for (Character c : delta.get(thisInitial).keySet()) {
			Triple<State, Character, State> t = new Triple<>(s, c, (State) delta.get(thisInitial).get(c).toArray()[0]);
			tSet.add(t);
			}
		}
		
		for (State s : this.states) {
			if (s.isInitial()) {
				State aux = new State(s.getName(), false, s.isFinal());
				sSet.add(aux);
			}else {
			sSet.add(s);
			}
		}
		
		
		DFA nuevo = new DFA(sSet, alphabet, tSet);	
		return nuevo;		
	}
	
	/**
	 * Returns a new automaton which recognizes the union of both
	 * languages, the one accepted by 'this' and the one represented
	 * by 'other'. 
	 * 
	 * @returns a new DFA accepting the union of both languages.
	 */	
	public DFA union(DFA other) {
		assert repOk();
		assert other.repOk();
				
		Set<Triple<State, Character, State>> tSet = new HashSet<>();
		Set<State> sSet = new HashSet<>();
		
		for(Triple<State, Character, State> t : this.getTransitions()) {
			tSet.add(t);
		}
		for(Triple<State, Character, State> t : other.getTransitions()) {
			tSet.add(t);
		}
		
		Set<Character> newAlphabet = new HashSet<>();
		
		for (Character c: this.alphabet) {
			newAlphabet.add(c);
		}
		for (Character c: other.alphabet) {
			newAlphabet.add(c);
		} 
		
		State thisInitial = this.initialState();
		State otherInitial = other.initialState();
		
		Integer n = this.states.size()+other.states.size()+1;
		String g = n.toString();
		Boolean b = (thisInitial.isFinal()||otherInitial.isFinal());
		
		State newOrigin = new State("q"+g, true, b);
		sSet.add(newOrigin);
		
		for (Character c : delta.get(thisInitial).keySet()) {
			Triple<State, Character, State> t = new Triple<>(newOrigin, c, (State) delta.get(thisInitial).get(c).toArray()[0]);
			tSet.add(t);
		}
		
		for (Character c : other.delta.get(otherInitial).keySet()) {
			Triple<State, Character, State> t = new Triple<>(newOrigin, c, (State) other.delta.get(otherInitial).get(c).toArray()[0]);
			tSet.add(t);
		}
		
		for (State s : this.states) {
			if (s.isInitial()) {
				State aux = new State(s.getName(), false, s.isFinal());
				sSet.add(aux);
			}else {
			sSet.add(s);
			}
		}
		
		for (State s : other.states) {
			if (s.isInitial()) {
				State aux = new State(s.getName(), false, s.isFinal());
				sSet.add(aux);
			}else {
			sSet.add(s);
			}
		}
		
		DFA nuevo = new DFA(sSet, newAlphabet, tSet);	
		return nuevo;
	}
	
	@Override
	public boolean repOk() {
		return !checkLambda() && checkInitialStates() && checkCorrectTransitions() && checkDeterministicTransitions();
	}
		
	

}
