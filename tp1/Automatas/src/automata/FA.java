package automata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import utils.Triple;

public abstract class FA {

	public static final Character Lambda = '_';
	
	protected Set<State> states;
	
	protected Set<Character> alphabet;
	
	protected boolean isDFA;
	
	protected boolean isNFA;
	
	protected boolean isNFALambda;
	
	//All states in the map must belong to states  set and all symbols must belong to the alphabet
	protected HashMap<State, HashMap<Character, Set<State>>> delta;
	
	/* Creation */
	
	/**	Parses and returns a finite automaton from the given file. The type of
	 * the automaton returned is the appropriate one for the automaton represented
	 * in the file (i.e. if the file contains the representation of an 
	 * automaton that is non-deterministic but has no lambda transitions, then an
	 * instance of NFA must be returned).
	 * 
	 * @param path Path to the file containing the specification of an FA.
	 * @return An instance of DFA, NFA or NFALambda, corresponding to the automaton
	 * represented in the file.
	 * @throws Exception Throws an exception if there is an error during the parsing process.
	 */
	public static FA parseFromFile(String path) throws Exception {
		try {
			Set<State> states = new HashSet<>();
			Set<Character> alphabet = new HashSet<>();
			Set<Triple<State,Character,State>> transitions = new HashSet<>();
			String departureStName = null;
			String arrivalStName = null;
			Set<String> finalStsNames = new HashSet<>();
			String initialStName = "";
			State initialState = null;
			Scanner firstScanner = new Scanner(new File("tp1/Automatas/"+path+".dot"));
			while(firstScanner.hasNextLine()) {
				String line = firstScanner.nextLine().replaceAll("\\s+","");
				if (line.contains("[shape=doublecircle]")) {
					String newLine = line.replace('[', '/');
					String[] res = newLine.split("/");
					finalStsNames.add(res[0]);
				}
			}
			firstScanner.close();
			Scanner secondScanner = new Scanner(new File("tp1/Automatas/"+path+".dot"));
			while(secondScanner.hasNextLine()) {
				String line = secondScanner.nextLine().replaceAll("\\s+","");
				if (line.contains("inic->")) {
					String[] res = line.split("->");
					initialStName = res[1].substring(0, res[1].length()-1);
					if (finalStsNames.contains(initialStName))
						initialState = new State(initialStName, true, true);
					else
						initialState = new State(initialStName, true, false);
					states.add(initialState);
				}
				else if (line.contains("->")) {
					String newLine = line.replaceAll("[->=]","/");
					newLine = newLine.replace('[','/').replace('"','/');
					String[] res = newLine.split("/");
					departureStName = res[0];
					arrivalStName = res[2];
					Character symbol = res[5].charAt(0);
					State departureState;
					State arrivalState;
					if (departureStName.equals(initialStName)) {
						departureState = initialState;
					}
					else {
						if (finalStsNames.contains(departureStName))
							departureState = new State(departureStName, false, true);
						else
							departureState = new State(departureStName, false, false);
						states.add(departureState);
					}
					if (arrivalStName.equals(initialStName)) {
						arrivalState = initialState;
					}
					else {
						if (finalStsNames.contains(arrivalStName))
							arrivalState = new State(arrivalStName, false, true);
						else
							arrivalState = new State(arrivalStName, false, false);
						states.add(arrivalState);
					}
					alphabet.add(symbol);
					transitions.add(new Triple<>(departureState,symbol,arrivalState));
				}
			}
			secondScanner.close();
			if (alphabet.contains(Lambda))
				return new NFALambda(states, alphabet, transitions);
			else {
				for (Triple<State,Character,State> t1 : transitions) {
					for (Triple<State,Character,State> t2 : transitions) {
						if (t1.first().equals(t2.first()) && t1.second().equals(t2.second()) && !t1.third().equals(t2.third())) {
							return new NFA(states, alphabet, transitions);
						}
					}
				}
				return new DFA(states, alphabet, transitions);
			}
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
		
	/**	
	 * Save given automaton dot description in the specified file
	 * @param path Path to the file to save a FA.
	 * @throws Exception Throws an exception if there is an error during writing file.
	 */
	public static  void writeToFile(String path, String dotCode) throws Exception {
		try {
			FileWriter fileWriter=new FileWriter("tp1/Automatas/"+path+".dot");
			fileWriter.write(dotCode);
			fileWriter.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Returns the DOT code representing the automaton.
	 */	
	public String toDot() {
		assert repOk();
		String dotString = "digraph {\n";
		dotString+="inic[shape=point];\n";
		dotString+="inic->"+initialState()+";\n";
		for (Triple<State,Character,State> t : getTransitions()) {
			dotString+=t.first()+"->"+t.third()+"[label=\""+t.second()+"\"];\n";
		}
		dotString+="\n";
		for (State s : finalStates()) {
			dotString+=s+"[shape=doublecircle];\n";
		}
		dotString+="}";
		return dotString;
	}
	
	
	/**
	 * @return the atomaton's set of states.
	 */
	public Set<State> getStates(){
		
		return states;
	}
	
	
	/**
	 * @return the atomaton's alphabet.
	 */
	public Set<Character> getAlphabet(){
		
		return alphabet;
		
	}
	
	/**
	 * @return the atomaton's initial state.
	 */
	public State initialState(){
		for (State s: states) {
			if (s.isInitial()) 
				return s;
		}
		return null;
	}
	
	/**
	 * @return the atomaton's final states.
	 */
	public Set<State> finalStates() {
		Set<State> finals = new HashSet<>();
		for (State s: states) {
			if (s.isFinal())
				finals.add(s);
				
		}
		return finals;
	}
	
	/**
	 * Query for the automaton's transition function.
	 * 
	 * @return A set of states (when FA is a DFA this method return a 
	 * singleton set) corresponding to the successors of the given state 
	 * via the given character according to the transition function.
	 */
	public  Set<State> delta(State from, Character c){
		assert states.contains(from);
		assert alphabet.contains(c);
		Set<State> arrivalStates = delta.get(from).get(c);
		if (arrivalStates!=null)
			return arrivalStates;
		return new HashSet<>();

	} 
	
	/*
	 * 	Automata Methods 
	*/
	
	
	/**
	 * Tests whether a string belongs to the language of the current 
	 * finite automaton.
	 * 
	 * @param string String to be tested for acceptance.
	 * @return Returns true iff the current automaton accepts the given string.
	 */
	public abstract boolean accepts(String string);
	
	/**
	 * Verifies whether the string is composed of characters in the alphabet of the automaton.
	 * 
	 * @return True iff the string consists only of characters in the alphabet.
	 */
	public boolean verifyString(String s) {
		
		for (int i = 0; i<s.length();i++) {
			if (!alphabet.contains(s.charAt(i))) {
				return false;
			}
		}
		return true;
		
	}
	
	/**
	 * @return True iff the automaton is in a consistent state.
	 */
	public abstract boolean repOk(); 
	
	/**
	*@returns true iff the alphabet contains lambda, ie. the automaton is a NFALambda.
	*/
	protected boolean checkLambda() {
		return alphabet.contains(Lambda);
	}
	
	/**
	 * @return true iff the automaton has only one initial state.
	 */
	protected boolean checkInitialStates() {
		int counter = 0;
		for (State s : states) {
			if (s.isInitial()) 
				counter++;
		}
		return counter==1;
	}
	
	/**
	 * @return true iff all transitions are only composed of states and characters 
	 * that are part of the state set and the alphabet respectively.
	 */
	protected boolean checkCorrectTransitions() {
		for (State from: delta.keySet()) {
			if (!states.contains(from))
				return false;
			HashMap<Character,Set<State>> snd = delta.get(from);
			for (Character c : snd.keySet()) {
				if (!alphabet.contains(c))
					return false;
				for (State dest : snd.get(c)) {
					if (!states.contains(dest))
						return false;
				}
			}
				
		}
		return true;
	}
	
	/**
	 * @return true iff the automaton is deterministic.
	 */
	protected boolean checkDeterministicTransitions() {
		for (State from: delta.keySet()) {
			HashMap<Character,Set<State>> snd = delta.get(from);
			for (Character c: snd.keySet()) {
				if (snd.get(c).size() >= 2)
					return false;
			}	
		}	
		return true;			
	}
	
	/**
	 * @return all transitions of FA
	 */
	protected Set<Triple<State,Character,State>> getTransitions(){
		Set<Triple<State,Character,State>> transitions = new HashSet<>();
		for (State s : delta.keySet()) {
			for (Character c : delta.get(s).keySet()) {
				Triple<State,Character,State> t = new Triple<>(s, c,(State) delta.get(s).get(c).toArray()[0]);
				transitions.add(t);
			}
		}
		
		return transitions;
	}
	
	public boolean isDFA() {
		return isDFA;
	}
	
	public boolean isNFA() {
		return isNFA;
	}
	
	public boolean isNFALambda() {
		return isNFALambda;
	}
}
