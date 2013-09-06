
package reconeixedormusica;

/**
 * Classe d'informació genèrica per a totes les classes. Es tracta de l'estàndard
 * de l'aplicació a l'hora de codificar audio.
 * @author Bernat i Joan
 */
public final class Patro {
     public static int CHUNK_SIZE = 4096;
     public static int LOWER_LIMIT = 40;
     public static int UPPER_LIMIT = 300;
     public static final int[] RANGE = new int[] {40,80,120,180, Patro.UPPER_LIMIT+1};

}