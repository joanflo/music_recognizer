
package reconeixedormusica;

/**
 * Representa el concepte de cançó dins el programa: una cançó està formada per
 * diferents tags com són el seu nom, el grup, l'àlbum... i també el nom de
 * l'arxiu del qual n'hem extret la informació
 * @author Bernat i Joan
 */
public class Canso {
    
    private String nomArxiu;
    private String nom;
    private String grup;
    private String album;
    private String any;
    private String duracio;

    
    /**
     * Set de totes les dades que conformen una cançó
     * @param nomArxiu nom de l'arxiu d'audio
     * @param nom nom cançó
     * @param grup grup/artista compositor
     * @param album àlbum al que pertany la cançó
     * @param any any cançó
     * @param duracio temps que dura
     */
    public Canso(String nomArxiu, String nom, String grup, String album, String any, String duracio) {
        this.nomArxiu = nomArxiu;
        this.nom = nom;
        this.grup = grup;
        this.album = album;
        this.any = any;
        this.duracio = duracio;
    }
    
    
    /**
     * Torna el nom de l'arxiu d'audio
     * @return nom arxiu
     */
    public String getNomArxiu() {
        return nomArxiu;
    }

    
    /**
     * Torna el nom de la cançó
     * @return nom
     */
    public String getNom() {
        return nom;
    }
    
    
    /**
     * Torna el nom del grup/artista
     * @return grup
     */
    public String getGrup() {
        return grup;
    }
    
    
    /**
     * Torna l'àlbum de la cançó
     * @return àlbum
     */
    public String getAlbum() {
        return album;
    }

    
    /**
     * Torna l'any en que es va fer la cançó
     * @return any
     */
    public String getAny() {
        return any;
    }

    
    /**
     * Torna la duració de la cançó
     * @return duracio
     */
    public String getDuracio() {
        return duracio;
    }
    
}