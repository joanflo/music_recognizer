/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package reconeixedormusica;

import javax.swing.JLabel;

/**
 * Classe que dibuixa gràfics durant una gravació amb micròfon
 * @author joanflo
 */
public class GraphicSound {
    
    private static int MAX_FREQUENCE = 800;
    private byte[] buffer;
    private int cont;
    private int anterior = 0;
    private JLabel j;
    
    
    /**
     * Constructor
     * @param jl
     * @param b
     * @param c 
     */
    public GraphicSound(JLabel jl, byte[] b, int c) {
        j = jl;
        buffer = b;
        cont = c;
    }

    /**
     * Dibuixa gràfic
     * @param i 
     */
    public void printSound(int i) {
        j.getGraphics().copyArea(0, 0, j.getWidth(), j.getHeight(), 10, 0);
        j.getGraphics().drawLine(10, j.getHeight() / 2 + j.getHeight() * i / MAX_FREQUENCE, 14, j.getHeight() / 2 + j.getHeight() * anterior / MAX_FREQUENCE);
        anterior = i;
    }
    
}