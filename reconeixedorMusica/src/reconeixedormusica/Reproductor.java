
package reconeixedormusica;

import java.io.File;
import java.util.Map;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerListener;


/**
 * Aquesta classe s'encarrega d'instanciar i controlar un player baixat
 * d'internet (http://www.javazoom.net/jlgui/jlgui.html) que proporciona
 * funcionalitats a alt nivell
 * @author Bernat i Joan
 */
public class Reproductor implements BasicPlayerListener {
    
    private boolean reproduintCanso;
    private BasicPlayer player;
    private int duracio;
    private Controlador controlador;
    
    
    /**
     * Instanciam el player i indicam que de moment no es reprodueix cap cançó
     */
    public Reproductor() {
        player = new BasicPlayer();
        reproduintCanso = false;
        player.addBasicPlayerListener(this);
    }
    
    
    /**
     * Obrim la cançó
     * @param canso cançó a obrir
     * @return resultat de la operació
     */
    public boolean obrirCanso(File canso, int duracio) {
        try {
            player.open(canso);
            reproduintCanso = false;
            this.duracio = duracio;
            return true;
        } catch (BasicPlayerException ex) {
            return false;
        }
    }
    
    
    /**
     * Reproduïm la cançó
     * @return resultat de la operació
     */
    public boolean reproduirCanso() {
        try {
            //per reproduïr una nova cançó aturam l'actual:
            if (reproduintCanso) {
                player.stop(); 
            }
            player.play();
            player.resume();
            reproduintCanso = true;
            return true;
        } catch (BasicPlayerException ex) {
            reproduintCanso = false;
            return false;
        }
    }
    
    
    /**
     * Aturam la cançó
     * @return resultat de la operació
     */
    public boolean aturarCanso() {
        try {
            player.stop();
            reproduintCanso = false;
            return true;
        } catch (BasicPlayerException ex) {
            reproduintCanso = false;
            return false;
        }
    }
    
    
    /**
     * Pausam la reproducció actual de la cançó
     * @return resultat de la operació
     */
    public boolean pausarCanso() {
        try {
            player.pause();
            reproduintCanso = false;
            return true;
        } catch (BasicPlayerException ex) {
            reproduintCanso = true;
            return false;
        }
    }
    
    
    /**
     * Indica si hi ha una cançó reproduint-se actualment
     * @return 
     */
    public boolean estamReproduintCanso() {
        return reproduintCanso;
    }
    
    
    /**
     * Set de la variable que indica si hi ha una reproducció en curs
     * @param reproduint 
     */
    public void setReproduintCanso(boolean reproduint) {
        this.reproduintCanso = reproduint;
    }
    
    
    /**
     * Aquest mètode es cridat diverses vegades per segon per informar del
     * progrès en la reproducció
     * @param bytesread bytes llegits fins el moment
     * @param microseconds temps transcorregut fins el moment
     * @param pcmdata /
     * @param properties propietats
     */
    @Override
    public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {
        int seconds = (int) (microseconds/1000000);
        controlador.updateBarraReproduccio(seconds+1, duracio);
    }
    
    
    /**
     * Es crida quan s'obri la cançó
     * @param o
     * @param map 
     */
    @Override
    public void opened(Object o, Map map) {
        //TODO
    }
    
    
    /**
     * Quan s'actualitza l'estat
     * @param bpe 
     */
    @Override
    public void stateUpdated(BasicPlayerEvent bpe) {
        //TODO
    }
    
    
    /**
     * Quan es configura el controlador
     * @param bc 
     */
    @Override
    public void setController(BasicController bc) {
        //TODO
    }
    
    
    /**
     * Set del controlador local (MVC)
     * @param controlador 
     */
    public void setControlador(Controlador controlador) {
        this.controlador = controlador;
    }
    
    
}