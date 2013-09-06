
package reconeixedormusica;

/**
 * Representa la coincidencia d'una cançó i la quantitat
 * de coincidències de DataPoints
 * base de dades
 * @author Bernat i Joan
 */
public class Match {

    private int songId;
    private int numCoincidencies;
    private Vista vista;

    /**
     * Constructor de la classe
     * @param songId valor inicial de songId
     * @param cantMatches valor inicial de la quantitat de coincidencies
     */
    public Match(int songId, int cantMatches) {
        this.songId = songId;
        this.numCoincidencies = cantMatches;
    }

    /**
     * Getter del nombre de coincidencies
     */
    public int getNumCoincidencies() {
        return numCoincidencies;
    }

    /**
     * Mètode per incrementar el nombre de coincidències
     */
    public void incrementaNumCoincidencies() {
        numCoincidencies++;
    }

    /**
     * Mètode per imprimir la informació de les coincidències en un format adient
     */
    public String imprimirMatch(){
        return "La cançó "+songId+" ha obtingut "+numCoincidencies;
    }

    /**
     * Getter del songId
     * @return el songId d'aquest Match
     */
    public int getSongId() {
        return songId;
    }
    
    
    
}
