package parser;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import automata.DFA;
import automata.FA;
import automata.RegexParser;
import automata.State;
import utils.Triple;

public class RegexParserToDFATest {
	
	@Test
	public void test1() throws Exception {
		RegexParser p = new RegexParser();
		DFA dfa = p.fromRegexToDFA("a.d.(a.d)*");
		State state = new State("q0",true,true);
		Set<State> states = new HashSet<>();
		Set<Triple<State,Character,State>> transitions = new HashSet<>();
		Set<Character> regexAlph = new HashSet<>();
			for(int i =97; i<=122;i++) {
				regexAlph.add((char)i);
			}
		for (Character c : regexAlph) {
			transitions.add(new Triple<>(state,c,state));
		}
		states.add(state);
		DFA sigmaStar = new DFA(states, regexAlph, transitions);
		/*State state2 = new State("q0",true,true);
		Set<State> otherStates = new HashSet<>();
		otherStates.add(state2);
		Set<Triple<State,Character,State>> otherTransitions = new HashSet<>();
		for (Character c : regexAlph) {
			otherTransitions.add(new Triple<>(state,c,state));
		}
		DFA sigmaStar2 = new DFA(otherStates, regexAlph, otherTransitions);*/
		FA.writeToFile("nuevoAutomata", sigmaStar.concat(dfa).toDot());
		//FA.writeToFile("nuevoAutomata", p.fromRegexToDFA("(a.d)*").toDot());
		//assertTrue(p.fromRegexToDFA("(a+b)*.c*").accepts("aabababa"));
	}
	
}
