package utils;

/*Esta clase es utlizada para manipular una cadena. Provee funcionalidades 
 * para obtener el token corriente de una cadena dada y "avanzar" en una cadena dada 
 * (descartar el primer caracter).
 * 
 * */
public class WordUtils {

		/**	 
		 * @return primer caracter de la cadena  dada
		 */
		public static char token(String w) throws IllegalArgumentException{
			if (w!=null && w.length()>0) {
				return w.charAt(0);
			}	
			else throw new IllegalArgumentException();
			
		}
		/**	
		 * @return  la cadena dada descartando el primer caracter
		 */
		public static String moveForward(String w) throws IllegalArgumentException{
			if (w!=null && w.length()>0) {
				return w.substring(1);
			}else
				throw new IllegalArgumentException();
			
		}
		
	    public static  String prepareWord(String w) {
	    		return w + "#";
	    	}
		
	
	
}
