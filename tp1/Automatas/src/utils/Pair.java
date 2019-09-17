package utils;

import automata.DFA;

/*Esta clase es utilizada por el parser descendente recursivo para mantener
 *la cadena  de entrada que aun no ha sido vista y el autómata 
 *construido en un determinado estado. 
 **/
public class Pair {

	private DFA af;
	private String currentWord;
	
	public Pair(DFA af, String currentWord) {
		this.af = af;
		this.currentWord = currentWord;
	}
	public DFA getAf() {
		return af;
	}
	public void setAf(DFA af) {
		this.af = af;
	}
	public String getCurrentWord() {
		return currentWord;
	}
	public void setCurrentWord(String currentWord) {
		this.currentWord = currentWord;
	}
	
	
	
}
