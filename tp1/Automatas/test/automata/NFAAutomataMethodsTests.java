package automata;
import static org.junit.Assert.*;

import org.junit.Test;

public class NFAAutomataMethodsTests { 

	// Tests for NFA1
	
	@Test
	public void test1() throws Exception {
		NFA nfa = (NFA) FA.parseFromFile("test/nfa1");
		assertTrue(nfa.accepts("ab"));
	}
	
	@Test 
	public void test2() throws Exception {
		NFA nfa = (NFA) FA.parseFromFile("test/nfa1");
		assertTrue(nfa.accepts("abaaaaaaaa"));
	}
	
	@Test
	public void test3() throws Exception {
		NFA nfa = (NFA) FA.parseFromFile("test/nfa1");
		assertFalse(nfa.accepts("abaaaaaaab"));
	}
	
	// Tests for NFA2
	
	@Test
	public void test4() throws Exception {
		NFA nfa = (NFA) FA.parseFromFile("test/nfa2");
		assertTrue(nfa.accepts("a"));
	}
	
	@Test
	public void test5() throws Exception {
		NFA nfa = (NFA) FA.parseFromFile("test/nfa2");
		assertTrue(nfa.accepts("aaaaaaaaaaaaaaa"));
	}	
	
	@Test
	public void test6() throws Exception {
		NFA nfa = (NFA) FA.parseFromFile("test/nfa2");
		assertFalse(nfa.accepts(""));
	}
	
}
