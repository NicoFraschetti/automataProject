package automata;

import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
	
	public static void clearScreen() {  
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();  
	}

	public static void menu() {
		Scanner input = new Scanner(System.in);
		int choice=0;
		FA automaton = null;
		do {
			clearScreen();
			System.out.println("\n");
			System.out.println("1 - Leer archivo dot");
			System.out.println("2 - Procesar cadena");
			System.out.println("3 - Convertir a DFA");
			System.out.println("4 - Obtener automata complemento (si el automata no es DFA sera convertido)");
			System.out.println("5 - Obtener clausura de Kleene del automata");
			System.out.println("6 - Obtener automata union");
			System.out.println("7 - Verificar si L(DFA) es finito");
			System.out.println("8 - Verificar si L(DFA) es vacio");
			System.out.println("9 - Escribir automata en archivo");
			System.out.println("10 - Salir");
			choice = Integer.valueOf(input.nextLine());
			
			switch (choice) {
				case 1:
					clearScreen();
					System.out.println("Ingrese el nombre del archivo (sin extension)");
					System.out.println("debe estar en tp1/Automatas/test");
					String path = "test/"+input.nextLine();
					try {
						automaton = FA.parseFromFile(path);
					}
					catch(FileNotFoundException e) {
						System.out.println("Hubo un problema al leer el archivo");
					} catch (Exception e) {
						e.printStackTrace();
					}
					input.nextLine();
					break;
				case 2:
					clearScreen();
					if (automaton==null) 
						System.out.println("Archivo no leido");
					else {
						System.out.println("Ingrese la cadena a procesar");
						String str = input.nextLine();
						if (automaton.accepts(str))
							System.out.println("cadena aceptada");
						else
							System.out.println("cadena no aceptada");
					}
					input.nextLine();
					break;
				case 3:
					clearScreen();
					if (automaton==null) 
						System.out.println("Archivo no leido");
					else {
						if (automaton.isDFA())
							System.out.println("El automata ya es DFA");
						else if (automaton.isNFA()) {
							automaton = ((NFA) automaton).toDFA();
							System.out.println("El automata fue convertido a DFA");
						}
						else {
							automaton = ((NFALambda) automaton).toDFA();
							System.out.println("El automata fue convertido a DFA");
						}
					}
					input.nextLine();
					break;
				case 4:
					clearScreen();
					if (automaton==null) 
						System.out.println("Archivo no leido");
					else {
						if (automaton.isDFA())
							automaton = ((DFA) automaton).complement();
						else if (automaton.isNFA()) {
							automaton = (((NFA) automaton).toDFA()).complement();
						}
						else {
							automaton = (((NFALambda) automaton).toDFA()).complement();
						}
					}
					input.nextLine();
					break;
				case 5:
					clearScreen();
					if (automaton==null) 
						System.out.println("Archivo no leido");
					else {
						if (automaton.isDFA())
							automaton = ((DFA) automaton).star();
						else if (automaton.isNFA()) {
							automaton = (((NFA) automaton).toDFA()).star();
						}
						else {
							automaton = (((NFALambda) automaton).toDFA()).star();
						}
					}
					input.nextLine();
					break;
				case 6:
					clearScreen();
					if (automaton==null) 
						System.out.println("Archivo no leido");
					else {
						System.out.println("Ingrese el archivo dot del automata para calcular la union");
						path = "test/"+input.nextLine();
						try {
							FA automaton2 = FA.parseFromFile(path);
							if (automaton.isNFA()) {
								automaton = ((NFA) automaton).toDFA();
							}
							else if (automaton.isNFALambda()){
								automaton = ((NFALambda) automaton).toDFA();
							}
							if (automaton2.isNFA()) {
								automaton2 = ((NFA) automaton).toDFA();
							}
							else if (automaton2.isNFALambda()){
								automaton2 = ((NFALambda) automaton).toDFA();
							}
							automaton = ((DFA) automaton).union((DFA) automaton2);
						}
						catch(FileNotFoundException e) {
							System.out.println("Archivo no encontrado");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
						
					input.nextLine();
					break;
				case 7:
					clearScreen();
					if (automaton==null) 
						System.out.println("Archivo no leido");
					else {
						if (automaton.isNFA()) {
							automaton = ((NFA) automaton).toDFA();
						}
						else if (automaton.isNFALambda()){
							automaton = ((NFALambda) automaton).toDFA();
						}
						System.out.println("El lenguaje es finito:"+((DFA) automaton).isFinite());
					}
					input.nextLine();
					break;
				case 8:
					clearScreen();
					if (automaton==null) 
						System.out.println("Archivo no leido");
					else {
						if (automaton.isNFA()) {
							automaton = ((NFA) automaton).toDFA();
						}
						else if (automaton.isNFALambda()){
							automaton = ((NFALambda) automaton).toDFA();
						}
						System.out.println("El lenguaje es vacio:"+((DFA) automaton).isEmpty());
					}
					input.nextLine();
					break;
				case 9:
					clearScreen();
					if (automaton==null) 
						System.out.println("Archivo no leido");
					{
						System.out.println("Ingrese el nombre del archivo (sin extension)");
						path = "test/"+input.nextLine();
						try {
							FA.writeToFile(path, automaton.toDot());
							System.out.println("Archivo creado con exito");
						}
						catch(Exception e) {
							System.out.println("Algo fallo al intentar escribir");
						}
					}
					input.nextLine();
					break;
			}
		}while(choice!=10);
		input.close();
	}
	
	public static void main(String[] args) {
		Main.menu();
	}

}
