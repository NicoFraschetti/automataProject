package automata;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import utils.Triple;


public class DFACreationTests {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCreation1() {
		Set<State> states = new HashSet<State>();
		Set<Character> alphabet = new HashSet<Character>();
		Set<Triple<State,Character,State>> transitions = new HashSet<Triple<State,Character,State>>();
		
		State q0 = new State("q0",true,false); //initial state
		State q1 = new State("q1",false,true); //final state
		State q2 = new State("q2",false,false);
		states.add(q0);
		states.add(q1);
		states.add(q2);
		alphabet.add('a');
		alphabet.add('b');
		transitions.add(new Triple(q0, 'a', q1));
		transitions.add(new Triple(q1, 'a', q2));
		transitions.add(new Triple(q2, 'a', q0));
		transitions.add(new Triple(q0, 'b', q0));
		transitions.add(new Triple(q1, 'b', q1));
		transitions.add(new Triple(q2, 'b', q2));
		
		DFA myDfa = new DFA(states, alphabet, transitions);
		
		assertTrue(myDfa.repOk());	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected=IllegalArgumentException.class)
	// An exception should be thrown if not state in the automaton was marked to be initial.
	public void testCreation2() {
		Set<State> states = new HashSet<State>();
		Set<Character> alphabet = new HashSet<Character>();
		Set<Triple<State,Character,State>> transitions = new HashSet<Triple<State,Character,State>>();
	
		
		State q1 = new State("q1",false,true); //final state
		states.add(q1);
		alphabet.add('a');
		transitions.add(new Triple(q1, 'a', q1));
		
		DFA myDfa = new DFA(states, alphabet, transitions);
	}

	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected=IllegalArgumentException.class)
	// The transitions should use only states that are listed in states
	public void testCreation3() {
		Set<State> states = new HashSet<State>();
		Set<Character> alphabet = new HashSet<Character>();
		Set<Triple<State,Character,State>> transitions = new HashSet<Triple<State,Character,State>>();

		
		State q0 = new State("q0",true,false);//initial state
		State q1 = new State("q1",false,true);//final state
		states.add(q0);
		alphabet.add('a');
		transitions.add(new Triple(q0, 'a', q0));
		transitions.add(new Triple(q1, 'a', q1));
		
		DFA myDfa = new DFA(states, alphabet, transitions);
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected=IllegalArgumentException.class)
	// The transitions should use alphabet characters listed in the alphabet 
	public void testCreation4() {
		Set<State> states = new HashSet<State>();
		Set<Character> alphabet = new HashSet<Character>();
		Set<Triple<State,Character,State>> transitions = new HashSet<Triple<State,Character,State>>();
		
		State q0 = new State("s0",true,false);
		states.add(q0);
		alphabet.add('a');
		transitions.add(new Triple(q0, 'b', q0));
	
		DFA myDfa = new DFA(states, alphabet, transitions);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected=IllegalArgumentException.class)
	// A DFA definition does not allow lambda in the alphabet
	public void testCreation5() {
		Set<State> states = new HashSet<State>();
		Set<Character> alphabet = new HashSet<Character>();
		Set<Triple<State,Character,State>> transitions = new HashSet<Triple<State,Character,State>>();
		 
		State q0 = new State("q0",true,true); //initial and final state
		states.add(q0);
		alphabet.add('a');
		transitions.add(new Triple(q0, FA.Lambda, q0));
	
		
		DFA myDfa = new DFA(states, alphabet, transitions);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected=IllegalArgumentException.class)
	// The transition function should be deterministic
	public void testCreation6() {
		Set<State> states = new HashSet<State>();
		Set<Character> alphabet = new HashSet<Character>();
		Set<Triple<State,Character,State>> transitions = new HashSet<Triple<State,Character,State>>();
		
		State q0 = new State("q0",true,false); //initial state
		State q1 = new State("q1",false,true); //final state
		State q2 = new State("q2",false,false);
		states.add(q0);
		states.add(q1);
		states.add(q2);
		alphabet.add('a');
		alphabet.add('b');
		transitions.add(new Triple(q0, 'a', q1));
		transitions.add(new Triple(q1, 'a', q2));
		transitions.add(new Triple(q2, 'a', q0));
		transitions.add(new Triple(q0, 'b', q0));
		transitions.add(new Triple(q1, 'b', q1));
		transitions.add(new Triple(q1, 'a', q1));
		transitions.add(new Triple(q2, 'b', q2));
		
		DFA myDfa = new DFA(states, alphabet, transitions);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected=IllegalArgumentException.class)
	// An exception should be thrown if more than one state in the automaton was marked to be initial.
	public void testCreation7() {
		Set<State> states = new HashSet<State>();
		Set<Character> alphabet = new HashSet<Character>();
		Set<Triple<State,Character,State>> transitions = new HashSet<Triple<State,Character,State>>();

		
		State q0 = new State("q0",true,false);//initial state
		State q1 = new State("q1",true,true);//final state
		states.add(q0);
		states.add(q1);
		alphabet.add('a');
		alphabet.add('b');
		transitions.add(new Triple(q0, 'a', q0));
		transitions.add(new Triple(q0, 'b', q1));
		
		DFA myDfa = new DFA(states, alphabet, transitions);
	}
		
	
}
