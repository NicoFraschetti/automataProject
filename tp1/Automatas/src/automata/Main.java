package automata;

public class Main {

	public static void main(String[] args) {
		RegexParser p = new RegexParser();
		try {
			System.out.println(p.mygrep("(a.c).(a.c)*","archivoPrueba"));
		}catch(RegexException e) {
			System.out.println(e.getMessage());
		}

	}

}
