
package reconeixedormusica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe que s'encarrega de fer els matchings entre els DataPoints
 * @author Bernat i Joan
 */
public class Matcher {

    static ArrayList<String> cansons = new ArrayList<String>();
    static HashMap<String, ArrayList<DataPoint>> mapa = new HashMap<String, ArrayList<DataPoint>>();

    /**
     * Getter de la llista de cançons de la base de dades
     * @return la llista de cançons de la base de dades
     */
    public static ArrayList getCansons() {
        return cansons;
    }

    /**
     * Donat un fitxer de base de dades es crea una taula de hashing
     * per a optimitzar la cerca de punts clau.
     * @param basedades Fitxer on trobar els datapoints de les cançons
     * @throws FileNotFoundException Pot ocoórrer que el fitxer indicat com
     * la base de dades no existeixi
     * @throws IOException Poden ocorrer errors d'entrada sortida a l'hora
     * de llegir el fitxer
     */
    public static void omplirTaula(String basedades) throws FileNotFoundException, IOException {
        File archivo = new File(basedades);
        BufferedReader lector = new BufferedReader(new FileReader(archivo));
        String lectura = lector.readLine();
        int time = 0, songID = 0;

        while (lectura != null) {
            String[] trossos = lectura.split("\t");
            if (trossos.length == 6) {
                if (mapa.containsKey(trossos[5])) {
                    DataPoint dp = new DataPoint(songID, time);
                    mapa.get(trossos[5]).add(dp);
                } else {
                    DataPoint dp = new DataPoint(songID, time);
                    ArrayList<DataPoint> l_aux = new ArrayList<DataPoint>();
                    l_aux.add(dp);
                    mapa.put(trossos[5], l_aux);
                }
                time++;
            } else {
                time = 0;
                songID++;
                cansons.add(lectura);
            }
            lectura = lector.readLine();
        }
    }

    /**
     * Donada una gravacio retorna una llista cansons amb el seus matchings
     * @param fitxerBaseDades Fitxer de texte amb els DataPoints de les cançons
     * de la base de dades
     * @param fitxerGravacio Fitxer de gravació o canço de la que es vol cercar
     * coincidència a la base de dades (Query By Humming)
     * @throws FileNotFoundException Error si no existeix el fitxer base de dades
     * @throws IOException Error que ocorre durant la lectura-escritura del fitxer
     */
    public static void cercaCoincidencies(Controlador c, String fitxerBaseDades,String fitxerGravacio) throws FileNotFoundException, IOException {
        omplirTaula(fitxerBaseDades);
        ArrayList<Match> coincidencies = new ArrayList<Match>();
        File archivo = new File(fitxerGravacio);
        BufferedReader lector = new BufferedReader(new FileReader(archivo));
        String lectura = lector.readLine();
        while (lectura != null) {
            String[] trossos = lectura.split("\t");
            if (trossos.length == 6) {
                if (mapa.containsKey(trossos[5])) {
                    ArrayList<DataPoint> laux = mapa.get(trossos[5]);
                    for (int i = 0; i < laux.size(); i++) {
                        DataPoint dpaux = laux.get(i);
                        int posicio = cansoJaTeCoincidencia(dpaux.getSongId(), coincidencies);
                        if (posicio >= 0) {
                            Match maux = coincidencies.get(posicio);
                            maux.incrementaNumCoincidencies();
                            coincidencies.set(posicio, maux);
                        } else {
                            coincidencies.add(new Match(dpaux.getSongId(), 0));
                        }
                    }
                }
            }
            lectura = lector.readLine();
        }
        if (coincidencies.isEmpty()) {
            System.out.println("No hi ha cap punt coincident en la gravació.");
        } else {
            for (int i = 0; i < coincidencies.size(); i++) {
                String m = coincidencies.get(i).imprimirMatch();
                c.informarUsuari(m);
            }
        }
        lector.close();
    }

    /**
     * Mètode per consulta si una clau és a la taula de hashing
     * @param hash Clau a cercar 
     * @return Vertader si existeix la clau al hashmap
     */
    public boolean consultaHash(String hash) {
        if (mapa.containsKey(hash)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Funcio booleana que indica sí una cançó ja tenia DataPoints coincidents
     * @param idCanso Identificador de la cançó a cercar
     * @param llistaCoincidencies Llista de coincidències on s'ha de mirar si
     * ja hi existeix la cançó que per id la idCanso
     * @return Vertader si la cançó ja tenia DataPoints coincidents
     */
    private static int cansoJaTeCoincidencia(int idCanso, ArrayList<Match> llistaCoincidencies) {
        boolean trobada = false;
        int comptador = 0;
        while (comptador < llistaCoincidencies.size() && !trobada) {
            if (idCanso == llistaCoincidencies.get(comptador).getSongId()) {
                trobada = true;
            }
            comptador++;
        }
        if (trobada) {
            return comptador - 1;
        } else {
            return -1;
        }
    }

    /**
     * Metode per imprimir els DataPoints de les diferents cançons que tenguin
     * la mateixa clau hash
     * @param hash És la clau de la qual es vol imprimir la llista de DataPoints
     */
    public static void imprimirDataPointsHash(String hash) {
        ArrayList<DataPoint> a = mapa.get(hash);
        for (int i = 0; i < a.size(); i++) {
            a.get(i).imprimirInfo();
        }
        System.out.print("\n");
    }
    
    
}
