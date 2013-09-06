
package reconeixedormusica;

/**
 * Classe que representa un punt clau en la cerca
 * @author Bernat i Joan
 */
class DataPoint {

    private int time;
    private int songId;

    /**
     * Constructor d'un DataPoint
     * @param songId identificador de la cançó
     * @param time instant de temps de la cançó
     */
    public DataPoint(int songId, int time) {
        this.songId = songId;
        this.time = time;
    }

    /**
     * Getter de l'instant de temps
     */
    public int getTime() {
        return time;
    }
    
    /**
     * Getter de la songId
     */
    public int getSongId() {
        return songId;
    }

    /**
     * Metode per imprimir la informació del DataPoint en forma de llista
     */
    public void imprimirInfo(){
        System.out.print("["+songId+"|"+time+"]->");
    }
    
    /**
     * Mètode que compara l'actual DataPoint amb el DataPoint paràmetre
     * @param dp2 és el DataPoint a comparar
     */
    public boolean iguals(DataPoint dp2){
        return this.songId==dp2.songId && this.time==dp2.time;
    }
}
