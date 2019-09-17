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
		Set<Character> newAlphabet = new HashSet<>();
		Set<Triple<State, Character, State>> newTransitions = new HashSet<>();
		Set<State> newStates = new HashSet<>();
		HashMap<String, State> statesMap = new HashMap<>();
		newAlphabet = alphabet;
		for (State s : states) 
			statesMap.put("a1"+s.getName(),new State("a1"+s.getName(),false,false));
		for (Triple<State, Character, State> t : getTransitions()) {
			newTransitions.add(new Triple<>(statesMap.get("a1"+(t.first().getName())),t.second(),statesMap.get("a1"+(t.third().getName()))));
		}
		State oldInitial = initialState();
		State newInitial = new State("q0",true,false);
		newStates.add(newInitial);
		newTransitions.add(new Triple<>(newInitial, Lambda, statesMap.get("a1"+oldInitial)));
		State newFinal = new State("qf",false,true);
		newStates.add(newFinal);
		newTransitions.add(new Triple<>(newInitial, Lambda, newFinal));
		for (State s : finalStates())
			newTransitions.add(new Triple<>(statesMap.get("a1"+s),Lambda,newFinal));
		for (State s : finalStates())
			newTransitions.add(new Triple<>(statesMap.get("a1"+s),Lambda,statesMap.get("a1"+oldInitial)));
		newStates.addAll(statesMap.values());
		return new NFALambda(newStates, newAlphabet, newTransitions).toDFA();
				
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
		Set<Character> newAlphabet = new HashSet<>();
		Set<Triple<State, Character, State>> newTransitions = new HashSet<>();
		Set<State> newStates = new HashSet<>();
		HashMap<String, State> statesMap = new HashMap<>();
		for (Character c : alphabet)
			newAlphabet.add(c);
		for (Character c : other.alphabet) 
			newAlphabet.add(c);
		for (State s : states) 
			statesMap.put("a1"+s.getName(),new State("a1"+s.getName(),false,false));
		for (State s : other.getStates())
			statesMap.put("a2"+s.getName(),new State("a2"+s.getName(),false,false));
		for (Triple<State, Character, State> t : getTransitions()) {
			newTransitions.add(new Triple<>(statesMap.get("a1"+(t.first().getName())),t.second(),statesMap.get("a1"+(t.third().getName()))));
		}
		for (Triple<State, Character, State> t : other.getTransitions()) {
			newTransitions.add(new Triple<>(statesMap.get("a2"+(t.first().getName())),t.second(),statesMap.get("a2"+(t.third().getName()))));
		}
		State oldInitial = initialState();
		State otherOldInitial = other.initialState();
		State newInitial = new State("q0",true,false);
		newStates.add(newInitial);
		newTransitions.add(new Triple<>(newInitial, Lambda, statesMap.get("a1"+oldInitial)));
		newTransitions.add(new Triple<>(newInitial, Lambda, statesMap.get("a2"+otherOldInitial)));
		State newFinal = new State("qf",false,true);
		newStates.add(newFinal);
		for (State s : finalStates())
			newTransitions.add(new Triple<>(statesMap.get("a1"+s), Lambda, newFinal));
		for (State s : other.finalStates())
			newTransitions.add(new Triple<>(statesMap.get("a2"+s), Lambda, newFinal));
		newStates.addAll(statesMap.values());
		return new NFALambda(newStates, newAlphabet, newTransitions).toDFA();
	}
	
	/**
	 * Returns a new automaton which recognizes the concatenation of both
	 * languages, the one accepted by 'this' and the one represented
	 * by 'other'. 
	 * 
	 * @returns a new DFA accepting the concatenation of both languages.
	 */	
	public DFA concat(DFA other) {
		assert repOk();
		assert other.repOk();
		Set<Character> newAlphabet = new HashSet<>();
		Set<Triple<State, Character, State>> newTransitions = new HashSet<>();
		Set<State> newStates = new HashSet<>();
		HashMap<String, State> statesMap = new HashMap<>();
		for (Character c : alphabet)
			newAlphabet.add(c);
		for (Character c : other.alphabet) 
			newAlphabet.add(c);
		for (State s : states) 
			statesMap.put("a1"+s.getName(),new State("a1"+s.getName(),s.isInitial(),false));
		for (State s : other.getStates())
			statesMap.put("a2"+s.getName(),new State("a2"+s.getName(),false,s.isFinal()));
		for (Triple<State, Character, State> t : getTransitions()) {
			newTransitions.add(new Triple<>(statesMap.get("a1"+(t.first().getName())),t.second(),statesMap.get("a1"+(t.third().getName()))));
		}
		for (Triple<State, Character, State> t : other.getTransitions()) {
			newTransitions.add(new Triple<>(statesMap.get("a2"+(t.first().getName())),t.second(),statesMap.get("a2"+(t.third().getName()))));
		}
		State oldInitial = other.initialState();
		for (State s : finalStates()) {
			newTransitions.add(new Triple<>(statesMap.get("a1"+(s.getName())), Lambda,statesMap.get("a2"+(oldInitial.getName()))));
		}
		newStates.addAll(statesMap.values());
		return new NFALambda(newStates, newAlphabet, newTransitions).toDFA();		
	}

	/**
	 * Returns a new automaton which recognizes the char a
	 * @returns a new DFA accepting the char a.
	 */	

	public static DFA fromToken(char a) {
		State initialState = new State("q0", true, false);
		State finalState = new State("q1", false, true);
		Set<State> states = new HashSet<>();
		Set<Character> alphabet = new HashSet<>();
		states.add(initialState);
		states.add(finalState);
		alphabet.add(a);
		Set<Triple<State,Character,State>> transitions = new HashSet<>();
		Triple<State,Character,State> transition = new Triple<>(initialState, a, finalState);
		transitions.add(transition);
		return new DFA(states, alphabet, transitions);
	}
	
	/**
	 * Returns a new automaton which recognizes the following language:
	 * L(sigma*) . L(this) . L(sigma*)  where * is the start operation 
	 * and . concat operation
	 * @returns a new DFA accepting the described language.
	 */	

    public DFA toGeneralDFA(Set<Character> sigma) {
    		State state = new State("q0",true,true);
    		Set<State> states = new HashSet<>();
    		states.add(state);
    		Set<Triple<State,Character,State>> transitions = new HashSet<>();
    		for (Character c : sigma)
    			transitions.add(new Triple<>(state, c, state));
    		DFA automaton1 = new DFA(states, sigma, transitions);
    		DFA automaton2 = new DFA(states, sigma, transitions);
    		return automaton1.concat(this).concat(automaton2);
    }


	@Override
	public boolean repOk() {
		return !checkLambda() && checkInitialStates() && checkCorrectTransitions() && checkDeterministicTransitions();
	}
	

	

}
