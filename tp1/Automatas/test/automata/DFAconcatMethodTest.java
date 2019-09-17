package automata;

import static org.junit.Assert.*;

import org.junit.Test;

public class DFAconcatMethodTest {

	//Test for concat method
	
	@Test
	public void test1() throws Exception {
		DFA dfa1 = (DFA) FA.parseFromFile("test/queseyo1");
		DFA dfa2 = (DFA) FA.parseFromFile("test/queseyo2");
		DFA dfa3 = dfa1.concat(dfa2);
		assertTrue(dfa3.accepts("0102210101010110101010101010101010"));
		FA.writeToFile("test/queseyo1concat2", dfa3.toDot());
	}
	
}
