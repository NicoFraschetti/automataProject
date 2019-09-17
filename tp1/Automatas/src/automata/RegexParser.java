package automata;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import utils.Pair;

import static utils.WordUtils.*;
public class RegexParser {
	
	public  Set<Character> regexAlph = new HashSet<Character>();
	
	public RegexParser() {
		for(int i =97; i<=122;i++) {
			regexAlph.add((char)i);
		}
	}
	
	/* @param path Path to the input file containing the searching text.
	 * @return the number of first regex matching line. if no matching, it returns 0.
	 * @throws Exception Throws an exception if regex is not a well-formed 
	 * regular expression.
	 */
	public  int mygrep(String regex, String path) throws RegexException{
		
		DFA dfa= fromRegexToDFA(regex);
		if (dfa != null) { //well-formed regex
			DFA gdfa = dfa.toGeneralDFA(regexAlph);
			try {
				int lineNumber = 1;
				Scanner sc = new Scanner(new File("tp1/Automatas/"+path));
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (gdfa.accepts(line))
						return lineNumber;
					lineNumber++;
				}
				sc.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
		}else 
			throw new RegexException("bad-formed regular expression");
	
		return 0;
		
		
	 }
	
	 /* E->T E'
	  * E'-> '|' T E' |lambda
	  * T->F T'
	  * T'->. F T' |lambda
	  * F ->P H
	  * H-> * | lambda
	  * P-> (E) | L
	  * L -> a | b | c |...|z
	  */
	
    public DFA fromRegexToDFA(String w) {
		String word =prepareWord(w);
		Pair r = e(word); //llamada al parser descendente recursivo
		boolean b = r!=null && (token(r.getCurrentWord())=='#');
		if (b) {
			return r.getAf();
		}
		return null;
		
	}
     
    
    private Pair e(String word) {
    	char tc = token(word);
    	if (tc == '(' || regexAlph.contains(tc)) {
    		Pair tPair = t(word);
    		if (tPair == null)
    			return null;
    		Pair epPair = ep(tPair.getCurrentWord());
    		if (epPair == null)
    			return null;
    		else if (epPair.getAf() == null)
    			return new Pair(tPair.getAf(),epPair.getCurrentWord());
    		else
    			return new Pair(tPair.getAf().union(epPair.getAf()),epPair.getCurrentWord());
    	}
		return null;
	}
     
    private Pair ep(String word) {
    	char tc = token(word);
    	if (tc == '|') {
    		String str = moveForward(word);
    		Pair tPair = t(str);
    		if (tPair == null)
    			return null;
    		Pair epPair = ep(tPair.getCurrentWord());
    		if (epPair == null)
    			return null;
    		else if (epPair.getAf() == null)
    			return new Pair(tPair.getAf(),epPair.getCurrentWord());
    		else
    			return new Pair(tPair.getAf().union(epPair.getAf()),epPair.getCurrentWord());
    	}
    	else if (tc == '#' || tc == ')') {
    		return new Pair(null,word);
    	}
		return null;
	}
     
    private Pair t(String word) {
    	char tc = token(word);
    	if (tc == '(' || regexAlph.contains(tc)) {
			Pair fPair = f(word);
			if (fPair == null)
				return null;
			Pair tpPair = tp(fPair.getCurrentWord());
			if (tpPair == null)
				return null;
			else if (tpPair.getAf() == null)
				return new Pair(fPair.getAf(),tpPair.getCurrentWord());
			else
				return new Pair(fPair.getAf().concat(tpPair.getAf()),tpPair.getCurrentWord());
		}
		return null;
	}
	
    private Pair tp(String word) {
    	char tc = token(word);
    	if (tc == '.') {
    		String str = moveForward(word);
    		Pair fPair = f(str);
    		if (fPair == null)
				return null;
			Pair tpPair = tp(fPair.getCurrentWord());
			if (tpPair == null)
				return null;
			else if (tpPair.getAf() == null)
				return new Pair(fPair.getAf(),tpPair.getCurrentWord());
			else
				return new Pair(fPair.getAf().concat(tpPair.getAf()),tpPair.getCurrentWord());
    	}
    	else if (tc == '#' || tc == ')' || tc == '|') {
    		return new Pair(null,word);
    	}
		return null;
	}
    
    private Pair f(String word) {
    	char tc = token(word);
    	if (tc == '(' || regexAlph.contains(tc)) {
			Pair pPair = p(word);
			if (pPair == null)
				return null;
			Pair hPair = h(pPair.getCurrentWord());
			if (hPair == null)
				return null;
			else if (hPair.getAf() == null)
				return new Pair(pPair.getAf(),hPair.getCurrentWord());
			else
				return new Pair(pPair.getAf().star(),hPair.getCurrentWord());
		}
		return null;
	}

    private Pair h(String word) {
    	char tc = token(word);
    	if (tc == '*') {
    		String str = moveForward(word);
    		return new Pair(DFA.fromToken('a'),str);
    	}
    	else if (tc == '#' || tc == ')' || tc == '|' || tc == '.') {
    		return new Pair(null,word);
    	}
		return null;
	}
    
    private Pair p(String word) {
    	char tc = token(word);
    	if (tc == '(') {
    		String str = moveForward(word);
    		Pair ePair = e(str);
    		if (ePair == null)
    			return null;
    		tc = token(ePair.getCurrentWord());
    		if (tc == ')') {
    			str = moveForward(ePair.getCurrentWord());
    			return new Pair(ePair.getAf(),str);
    		}
    		return null;
    	}
    	else if (regexAlph.contains(tc)) {
    		return l(word);
    	}
		return null;
	}
    
    private Pair l(String word) {
    	char tc = token(word);
    	if (regexAlph.contains(tc)) {
    		String str = moveForward(word);
    		return new Pair(DFA.fromToken(tc), str);
    	}
		return null;
	}
}
