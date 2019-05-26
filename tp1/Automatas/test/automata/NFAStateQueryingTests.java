package automata;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import utils.Triple;


public class NFAStateQueryingTests {

	private NFA nfa;
	
	private State s0;
	
	private State s1;
	
	private State s2;
	
	
	@Before
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setUp() throws Exception {
		Set<State> states = new HashSet<State>();
		Set<Character> alphabet = new HashSet<Character>();
		Set<Triple<State,Character,State>> transitions = new HashSet<Triple<State,Character,State>>();
			
		State q0 = new State("s0",true,false); //Initial state
		State q1 = new State("s1",false,true);//Final state
		State q2 = new State("s2",false,false);
		
		states.add(q0);
		states.add(q1);
		states.add(q2);
		alphabet.add('a');
		alphabet.add('b');
		
		transitions.add(new Triple(q0, 'a', q1));
		transitions.add(new Triple(q1, 'a', q2));
		transitions.add(new Triple(q1, 'b', q2));
		transitions.add(new Triple(q2, 'a', q0));
		transitions.add(new Triple(q0, 'b', q0));
		transitions.add(new Triple(q1, 'b', q1));
		transitions.add(new Triple(q2, 'b', q2));
		
		this.s0 = q0;
		this.s1 = q1;
		this.s2 = q2;
		
		nfa = new NFA(states, alphabet, transitions);
	}
	
	@Test
	public void test1() {
		assertTrue(nfa.getStates().size() == 3);	
	}
	
	
	@Test
	public void test2() {
		Set<Character> set = new HashSet<Character>();
		set.add('a');
		set.add('b');
		assertTrue(nfa.getAlphabet().equals(set));	
	}	
	
	@Test
	public void test5() {
		assertTrue(nfa.delta(s0, 'a').size() == 1);
		assertTrue(nfa.delta(s1, 'b').size() == 2);
	}	
		
}
