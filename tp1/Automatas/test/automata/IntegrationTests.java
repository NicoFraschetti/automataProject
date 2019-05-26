package automata;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class IntegrationTests {
	
	static DFA myDfa;
	
	static NFA myNfa;
	
	static NFALambda myNfalambda;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		myDfa = (DFA) FA.parseFromFile("test/dfa1");
		myNfa = (NFA) FA.parseFromFile("test/nfa1");
		myNfalambda = (NFALambda) FA.parseFromFile("test/nfalambda1");
	}
	
	@Test
	public void test1() {
		assertTrue(myDfa.toNFA().accepts("ab"));
		assertTrue(myDfa.toNFA().accepts("abbbbb"));
		assertFalse(myDfa.toNFA().accepts("bbbbb"));
		assertFalse(myDfa.toNFA().accepts("a"));
	}
	@Test
	public void test2() throws Exception {
		assertTrue(myNfa.toDFA().accepts("ab"));
		assertTrue(myNfa.toDFA().accepts("abaaaaa"));
		assertFalse(myNfa.toDFA().accepts("abbbb"));
		assertFalse(myNfa.toDFA().accepts("a"));
		FA.writeToFile("test/deterministicnfa1", myNfa.toDFA().toDot());
	}
	
	@Test
	public void testPropio() throws Exception {
		NFA nfa = (NFA) FA.parseFromFile("test/nfa2cp3");
		assertTrue(nfa.accepts("1100"));
		DFA dfa = nfa.toDFA();
		assertTrue(dfa.accepts("1100"));
		FA.writeToFile("test/dnfa2cp3",nfa.toDFA().toDot());
	}
	
	@Test
	public void test3() {
		assertTrue(myNfalambda.toDFA().accepts("casa"));
		assertTrue(myNfalambda.toDFA().accepts("asa"));
		assertFalse(myNfalambda.toDFA().accepts("cas"));
		assertFalse(myNfalambda.toDFA().accepts("asac"));
	}
	
}
