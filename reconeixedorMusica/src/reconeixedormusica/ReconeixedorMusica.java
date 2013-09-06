
package reconeixedormusica;

/**
 * Classe des d'on s'inicia el programa
 * @author Bernat i Joan
 */
public class ReconeixedorMusica {

    
    /**
     * Aplicam el patró Model-Vista-Controlador
     */
    private ReconeixedorMusica() {
        
        //Al controlador l'hi passam el model:
        Reproductor reproductor = new Reproductor();
        Controlador controlador = new Controlador(reproductor);
        
        //La vista tindrà referenciat el controlador
        Vista vista = new Vista(controlador);
        vista.setVisible(true);
        
        //El controlador tindrà referenciada la vista:
        controlador.setVista(vista);
    }
    
    /**
     * Iniciam el programa
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new ReconeixedorMusica();
    }
}
