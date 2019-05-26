package automata;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;


import org.junit.Before;
import org.junit.Test;

import utils.Triple;


public class DFAStateQueryingTests {

	private DFA dfa;
	
	@Before
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setUp() throws Exception {
		Set<State> states = new HashSet<State>();
		Set<Character> alphabet = new HashSet<Character>();
		Set<Triple<State,Character,State>> transitions = new HashSet<Triple<State,Character,State>>();
		
		State q0 = new State("q0",true,false); //initial State
		State q1 = new State("q1",false,true); //final State
		State q2 = new State("q2", false,false);
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
		
		dfa = new DFA(states, alphabet, transitions);
	}
	
	@Test
	public void test1() {
		assertTrue(dfa.getStates().size() == 3);	
	}
	
	
	@Test
	public void test2() {
		Set<Character> set = new HashSet<Character>();
		set.add('a');
		set.add('b');
		assertTrue(dfa.getAlphabet().equals(set));	
	}	
		
}
