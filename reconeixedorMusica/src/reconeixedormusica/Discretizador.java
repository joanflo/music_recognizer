
package reconeixedormusica;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Classe per codificar tots els fitxers i gravacions al format adequat per al 
 * procés de matching
 * @author Bernat i Joan
 */
public class Discretizador {

    private static long hash;

    /**
     * Donada una frequencia retorna l'interval (definit a la classe Patro) en que es troba
     * @param freq Frequencia de la que s'ha de retornar el rang
     */
    public int getIndex(int freq) {
        int i = 0;
        while (Patro.RANGE[i] < freq) {
            i++;
        }
        return i;
    }

    /**
     * Donat un ByteArrayOutputStream corresponent a una gravació, se li assigna un títol
     * a la gravació i s'escriu en un fitxer de sortida.
     * @param gravacio És l'output stream a codificar
     * @param és el titol de la gravació
     * @fitxerSortida és el fitxer on es volcarà la transformació del fitxer
     */
    public void transformarGravacio(ByteArrayOutputStream gravacio, String titol, String fitxerSortida) throws IOException {
        File archivo = new File(fitxerSortida);
        BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo, true));
        escritor.write(titol);
        escritor.newLine();
        escritor.close();

        byte audio[] = gravacio.toByteArray();
        final int totalSize = audio.length;
        int amountPossible = totalSize / Patro.CHUNK_SIZE;
        //When turning into frequency domain we'll need complex numbers
        Complex[][] results = new Complex[amountPossible][];

        //For all the chunks:
        for (int times = 0; times < amountPossible; times++) {
            Complex[] complex = new Complex[Patro.CHUNK_SIZE];
            for (int i = 0; i < Patro.CHUNK_SIZE; i++) {
                //Put the time domain data into a complex number with imaginary part as 0:
                complex[i] = new Complex(audio[(times * Patro.CHUNK_SIZE) + i], 0);
            }
            //Perform FFT analysis on the chunk:
            results[times] = FFT.fft(complex);
            GetKeyPoints(results[times], amountPossible, fitxerSortida);
        }
        //System.exit(0); No ha d'acabar el programa
    }

    /**
     * Donat el path d'un fitxer es codifica i s'escriu en un fitxer de sortida
     * @param pathfwav Path del fitxer a codificar
     * @param titol Títol del so codificar
     * @param fitxerSortida Nom del fitxer de sortida on volcar el codificat
     * @throws IOException Excepció que pot ocorrer durant la codificacio
     * @throws UnsupportedAudioFileException Si el fomart d'audio no es pot tractar amb l'aplicació
     */
    public void transformarFitxer(String pathfwav, String titol, String fitxerSortida) throws IOException, UnsupportedAudioFileException {
        File archivo = new File(fitxerSortida);
        BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo, true));
        escritor.write(titol);
        escritor.newLine();
        escritor.close();

        byte audio[] = Conversor.WAVconvertirArraybytes(pathfwav);
        final int totalSize = audio.length;
        int amountPossible = totalSize / Patro.CHUNK_SIZE;
        //When turning into frequency domain we'll need complex numbers
        Complex[][] results = new Complex[amountPossible][];

        //For all the chunks:
        for (int times = 0; times < amountPossible; times++) {
            Complex[] complex = new Complex[Patro.CHUNK_SIZE];
            for (int i = 0; i < Patro.CHUNK_SIZE; i++) {
                //Put the time domain data into a complex number with imaginary part as 0:
                complex[i] = new Complex(audio[(times * Patro.CHUNK_SIZE) + i], 0);
            }
            //Perform FFT analysis on the chunk:
            results[times] = FFT.fft(complex);
            GetKeyPoints(results[times], amountPossible, fitxerSortida);
        }
    }

    /**
     * Aconsegueix els punts més significatius en una llista d'enters
     * @param result resultat d'una transformada de fourier
     * @param amountPossible Quantitat de punts possibles
     * @param fitxerSortida Fitxer on s'escriuen els punts
     * @return retorna la llista de punts significatius
     * @throws IOException  Es pot produïr una excepció d'entrada-sortida durant l'operació 
     * d'escriure al fitxer
     */
    public int[] GetKeyPoints(Complex[] result, int amountPossible, String fitxerSortida) throws IOException {

        int[] recordPoints = new int[]{0, 0, 0, 0, 0};
        double[] highscores = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};

        for (int freq = Patro.LOWER_LIMIT; freq < Patro.UPPER_LIMIT; freq++) {
            //Get the magnitude:
            double mag = Math.log(result[freq].abs() + 1);
            //Find out which range we are in:
            int index = getIndex(freq);
            //Save the highest magnitude and corresponding frequency:
            if (mag > highscores[index]) {
                highscores[index] = mag;
                recordPoints[index] = freq;
            }
        }
        hash = CreateHashValue(recordPoints);
        String s = "" + hash;
        File archivo = new File(fitxerSortida);
        BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo, true));
        //Write the points to a file:
        for (int i = 0; i < recordPoints.length; i++) {
            escritor.write(recordPoints[i] + "\t");
        }
        escritor.write(s);
        escritor.newLine();
        escritor.close();
        return recordPoints;
    }
    private final int FUZ_FACTOR = 2;

    /**
     * Funció de hashing que retorna una clau pseudo-única per a cada datapoints
     * @param array llista de frequencies d'una mostra
     * @return valor de la clau Hash
     */
    public long CreateHashValue(int[] array) {
        long p1 = (long) array[0];
        long p2 = (long) array[1];
        long p3 = (long) array[2];
        long p4 = (long) array[3];
        return (p4 - (p4 % FUZ_FACTOR)) * 100000000 + (p3 - (p3 % FUZ_FACTOR)) * 100000 + (p2 - (p2 % FUZ_FACTOR)) * 100 + (p1 - (p1 % FUZ_FACTOR));

    }
}
