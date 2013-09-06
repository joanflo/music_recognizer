
package reconeixedormusica;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JLabel;

/**
 * Classe per recollir gravacions des del micròfon
 * @author Bernat i Joan
 */
public class Gravadora {

    private static boolean running;
    private JLabel labelGrafics;
    private int comptador;
    
    
    /**
     * Constructor gravadora
     * @param j 
     */
    public Gravadora(JLabel j) {
        labelGrafics = j;
        comptador = 0;
    }

    /**
     * Mètode per definir el format d'un Audio
     * @return un format d'audio
     */
    private AudioFormat getFormat() {
        float sampleRate = 44100;
        int sampleSizeInBits = 16;
        int channels = 1; //mono
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    /**
     * Metode per gravar des del microfon duran els segons indicats per paràmetre
     * @param segons duració de la gravació
     * @return un stream de dades per a convertir
     * @throws LineUnavailableException Pot ocorrer que el hardware no estigui disponible per gravar
     * @throws IOException Es possible que es produeixin errors d'entrada sortida durant la gravació
     */
    public ByteArrayOutputStream gravar(int segons) throws LineUnavailableException, IOException {
        final AudioFormat format = getFormat(); //Fill AudioFormat with the wanted settings
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();

        // In another thread I start:
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        running = true;

        int bufferSize = (int) format.getSampleRate() * format.getFrameSize();

        byte[] buffer = new byte[bufferSize];

        
        try {
            float f = System.nanoTime();
            while (running) {
                int count = line.read(buffer, 0, buffer.length);
                if (count > 0) {
                    out.write(buffer, 0, count);
                }
                
                /*
                GraphicSound g = new GraphicSound(labelGrafics, buffer, comptador % 10);
                g.printSound(count);
                */
                
                if (System.nanoTime() - f > segons * 1000000000.0) {//5 segons && chunksize = 512 => 275+1 mostres
                    running = false;
                }
            }
            out.close();
            line.close();
        } catch (IOException e) {
            System.err.println("I/O problems: " + e);
            System.exit(-1);
        }
        
        return out;
    }
}
