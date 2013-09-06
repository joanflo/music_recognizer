
package reconeixedormusica;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Classe per a convertir diferents fitxer i formats a estàndards de l'aplicació
 * @author Bernat i Joan
 */
public class Conversor {
    
    static final int WAV_HEADER_SIZE = 44;

    /**
     * Transforma l'inputStream del fitxer especificat per el parametre fichero a un array de bytes
     * @param fichero fitxer del que s'ha d'extreure l'array de bytes
     */
    public static ByteArrayOutputStream inputStreamToByteArray(String fichero) throws IOException {
        InputStream inStream = new FileInputStream(fichero);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inStream.read(buffer)) > 0) {
            baos.write(buffer, 0, bytesRead);
        }
        inStream.close();
        baos.close();
        return baos;
    }

    /**
     * Transforma el fitxer especificat per el parametre fwav a un array de bytes
     * @param fwav fitxer del que s'ha d'extreure l'array de bytes
     */
    public static byte[] WAVconvertirArraybytes(String fwav) throws UnsupportedAudioFileException, IOException {
        //WAV Header is usually 44 bytes
        File musica = new File(fwav);
        AudioInputStream inputStream = AudioSystem.getAudioInputStream(musica);
        int numBytes = inputStream.available();
        byte[] buffer = new byte[numBytes];
        inputStream.read(buffer, 0, numBytes);
        inputStream.close();
        byte[] sortida = new byte[numBytes - WAV_HEADER_SIZE];
        //Calcul per copiar esta mal fet!!
        for (int i = 43; i < buffer.length - 1; i++) {
            sortida[i - 43] = buffer[i];
        }
        return sortida;
    }

    
    /**
     * Donat els path d'un fitxer origen d'audio i un fitxer de sortida d'audio
     * copia des de startSecond fins a secondsToCopy des de l'oigen al desti
     * @param sourceFileName fitxer origen
     * @param destinationFileName fitxer desti
     * @param startSecond indica el segon a partir del qual s'ha de copiar el fitxer origen
     * @param secondsToCopy indica quants segons d'audio s'han de copiar al fitxer destí
     */
    public static void copyAudio(String sourceFileName, String destinationFileName, int startSecond, int secondsToCopy) {
        AudioInputStream inputStream = null;
        AudioInputStream shortenedStream = null;
        try {
            File file = new File(sourceFileName);
            AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
            AudioFormat format = fileFormat.getFormat();
            inputStream = AudioSystem.getAudioInputStream(file);
            int bytesPerSecond = format.getFrameSize() * (int) format.getFrameRate();
            inputStream.skip(startSecond * bytesPerSecond);
            long framesOfAudioToCopy = secondsToCopy * (int) format.getFrameRate();
            shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
            File destinationFile = new File(destinationFileName);
            AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
        } catch (Exception e) {
            println(e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    println(e);
                }
            }
            if (shortenedStream != null) {
                try {
                    shortenedStream.close();
                } catch (Exception e) {
                    println(e);
                }
            }
        }
    }

    /**
     * Imprimeix una linea a la sortida amb l'objecte o 
     * @param o Objecte a imprimir
     */
    public static void println(Object o) {
        System.out.println(o);
    }

    /**
     * Imprimeix a la sortida l'objecte o 
     * @param o Objecte a imprimir
     */
    public static void print(Object o) {
        System.out.print(o);
    }
}
