package reconeixedormusica;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Classe amb el rol de controlador al patró MVC. S'encarrega dels esdeveniments
 * i de mantenir la comunicació entre les distintes classes que formen el model
 * així com la finestra gràfica
 * @author Bernat i Joan
 */
public class Controlador implements ActionListener {

    private static final int DURACIO_GRAVACIO = 10;
    
    private Vista vista = null;
    private Reproductor reproductor = null;
    private boolean cansoOberta;
    private ArrayList<Canso> llistaCansons;
    private File fitxerAudio;
    private LectorMetadata lectorMetadata;
    private String nomArxiuDarreraGravacio;
    private int posCanso;
    

    /**
     * Inicia els components del controldor
     * @param reproductor player audio
     */
    public Controlador(Reproductor reproductor) {
        this.reproductor = reproductor;
        reproductor.setControlador(this);
        cansoOberta = false;
        llistaCansons = new ArrayList<Canso>();
        nomArxiuDarreraGravacio = null;
    }
    
    
    /**
     * Es fa càrrec dels esdeveniments del programa
     * @param e esdeveniment disparat
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //Guardam dins l'string l'AcionComand
        String comand = e.getActionCommand();

        if (comand.equals("obrir_canso")) {
            tractarEsdevenimentObrirCanso();
        } else if (comand.equals("obrir_javadoc")) {
            obrirArxiu("dist/javadoc/index.html");
        } else if (comand.equals("obrir_manual")) {
            obrirArxiu("documentacio.pdf");
        } else if (comand.equals("info_programa")) {
            vista.mostrarMissatgeSobre();
        } else if (comand.equals("sortir")) {
            System.exit(0);//tancam la màquina virtual
        } else if (comand.equals("eliminar_canso")) {
            eliminarCanso();
        } else if (comand.equals("afegir_canso")) {
            try {
                afegirCanso();
            } catch (LineUnavailableException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (comand.equals("play_pause_canso")) {
            tractarEsdevenimentPlayPause();
        } else if (comand.equals("stop_canso")) {
            tractarEsdevenimentStop();
        } else if (comand.equals("gravar")) {
            try {
                gravar();
            } catch (LineUnavailableException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (comand.equals("comparar")) {
            try {
                comparar();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    /**
     * Es fa càrrec de l'esdeveniment d'obrir la cançó: mostra un file chooser
     * per a que l'usuari trïi un fitxer, es llegeix la seva metadata (per a
     * mostrarla per la interficie) i el player intenta obrir-la.
     */
    private void tractarEsdevenimentObrirCanso() {
        //L'usuari tria la cançó al file chooser:
        fitxerAudio = vista.mostrarFileChooser(true);
        
        if (fitxerAudio != null) {
            vista.informarUsuari("Obrint cançó " + fitxerAudio.getName());
            
            //mostram la metainformació:
            lectorMetadata = new LectorMetadata(fitxerAudio);
            vista.setInfoMetadata(lectorMetadata.getNom(), lectorMetadata.getGrup(), lectorMetadata.getAlbum(), lectorMetadata.getGenere(), lectorMetadata.getAny(), lectorMetadata.getCaratula());
            //el reproductor obri la cançó:
            boolean resultat = reproductor.obrirCanso(fitxerAudio, lectorMetadata.getDuracio());
            if (resultat == false) {
                vista.mostrarMissatgeAlerta("El reproductor no pot obrir la cançó");
            } else {
                cansoOberta = true;
                vista.informarUsuari("Cançó oberta correctament");
                vista.setBotoPlayPause(true);
            }
        }
    }
    
    
    /**
     * Es fa càrrec de l'esdeveniment de pausar/encendre la reproducció d'audio
     */
    private void tractarEsdevenimentPlayPause() {
        if (cansoOberta) {
            if (reproductor.estamReproduintCanso()) {
                boolean resultat = reproductor.pausarCanso();
                if (resultat == false) {
                    vista.mostrarMissatgeAlerta("El reproductor no pot pausar la cançó");
                } else {
                    vista.setBotoPlayPause(true);
                }
            } else {
                boolean resultat = reproductor.reproduirCanso();
                if (resultat == false) {
                    vista.mostrarMissatgeAlerta("El reproductor no pot reproduir la cançó");
                } else {
                    vista.setBotoPlayPause(false);
                }
            }
        }
    }
    
    
    /**
     * Es fa càrrec de l'esdeveniment stop de reproducció d'audio
     */
    private void tractarEsdevenimentStop() {
        if (cansoOberta) {
            boolean resultat = reproductor.aturarCanso();
            if (resultat == false) {
                vista.mostrarMissatgeAlerta("El reproductor no pot aturar la cançó");
            }
            vista.setBotoPlayPause(true);
            vista.updateBarraReproduccio(0, -1);//-1 és un conveni per no actualitzar el paràmetre
        }
    }
    
    
    /**
     * Mètode genèric per obrir arxius externs (com PDFs i HTMLs)
     * @param ruta path de l'arxiu
     */
    private void obrirArxiu(String ruta) {
        try {
            File path = new File(ruta);
            Desktop.getDesktop().open(path);
        } catch (IOException ex) {
            vista.mostrarMissatgeAlerta("Error al llegir del fitxer.");
        } catch (IllegalArgumentException ex) {
            vista.mostrarMissatgeAlerta("No s'ha pogut obrir el fitxer ja que no existeix.");
        }
    }
    
    
    /**
     * Set de la vista del MVC
     * @param vista 
     */
    public void setVista(Vista vista) {
        this.vista = vista;
    }
    
    
    /**
     * Actualitza la barra de reproducció de la vista
     * @param segonsTranscorreguts segons fins el present moment
     * @param duracioTotal segons totals de duració
     */
    public void updateBarraReproduccio(int segonsTranscorreguts, int duracioTotal) {
        vista.updateBarraReproduccio(segonsTranscorreguts, duracioTotal);
        //Miram si ja ha acabat sa cançó:
        if (segonsTranscorreguts == duracioTotal) {
            reproductor.setReproduintCanso(false);
        }
    }
    
    
    /*
     * Es comprova que l'usuari ha seleccionat una cançó per eliminar, s'agafa
     * la cançó seleccionada (cercant-la dins la llista de cançons de la BBDD) i
     * si es troba es procedeix a eliminar-la: s'ha d'eliminar tant del fitxer
     * on hi ha els datapoint's (BBDD) i del fitxer amb la info detallada de
     * les cançons. Finalment actualitzam la taula de la interficie.
     */
    private void eliminarCanso() {
        //Comprovam quina cançó es vol eliminar:
        Canso c = vista.getCansoSeleccionada();
        if (c != null) {
            vista.informarUsuari("Eliminant cançó '" + c.getNom() + "'...");
            
            //Cercam la cançó a la llista de cançons
            Canso cAux = cercarCanso(c);
            if (cAux != null) {
                String nomArxiu = cAux.getNomArxiu();
                
                //Eliminam la cançó del fitxer "BBDD.txt":
                borrarCanso("BBDD.txt", nomArxiu);
                
                //Eliminam la cançó del fitxer "info_musica.txt":
                borrarCanso("info_musica.txt", nomArxiu);

                //Eliminam la cançó del arraylist:
                llistaCansons.remove(posCanso);

                //Actualitzam la taula de la vista:
                vista.actualitzarTaulaCansons(llistaCansons);
                vista.informarUsuari("Cançó eliminada correctament");
            } else {
                vista.mostrarMissatgeAlerta("No s'ha pogut trobar la cançó al fitxer");
            }
        }
    }
    
    
    /**
     * Cerca una cançó que coincideixi amb la donada
     * @param c
     * @return 
     */
    private Canso cercarCanso(Canso c) {
        boolean trobada = false;
            posCanso = 0;
            Canso cAux = null;
            while (posCanso<llistaCansons.size() && !trobada) {
                
                cAux = llistaCansons.get(posCanso);
                if (sonIguals(c, cAux)) {
                    trobada = true;
                } else {
                    posCanso++;
                }
            }
            return cAux;
    }
    
    
    /**
     * Afegim la cançó que l'usuari té oberta en el moment. Això implica
     * codificar-la per a ficar-la amb els datapoint's al fitxer de la BBDD
     * i també ficar la info de la cançó (extreta de la seva metadata) al fitxer
     * corresponent. Finalment actualitzam la taula de la interficie.
     * @throws LineUnavailableException
     * @throws IOException
     * @throws UnsupportedAudioFileException 
     */
    private void afegirCanso() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        if (cansoOberta) {
            //Pillam l'arxiu de la cançó:
            String nomFitxer = fitxerAudio.getName();
            String path = fitxerAudio.getPath();

            //Codificam l'arxiu i el ficam dins el fitxer "BBDD.txt"
            insertarCansoFitxer(nomFitxer, path, "BBDD.txt");

            //Agafam la info de la cançó:
            String titol = lectorMetadata.getNom();
            String grup = lectorMetadata.getGrup();
            String album = lectorMetadata.getAlbum();
            String any = lectorMetadata.getAny();
            int duracio = lectorMetadata.getDuracio();

            //Inserim dins el fitxer "info_musica.txt" aquesta nova cançó:
            File archivo = new File("info_musica.txt");
            BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo, true));
            //ID cançó
            escritor.write(nomFitxer);
            escritor.newLine();
            //titol cançó
            escritor.write(titol);
            escritor.newLine();
            //nom grup
            escritor.write(grup);
            escritor.newLine();
            //àlbum
            escritor.write(album);
            escritor.newLine();
            //any cançó
            escritor.write(any);
            escritor.newLine();
            //Duració:
            int minuts = duracio / 60;
            int segons = duracio % 60;
            String strSegons = "" + segons;
            if (segons < 10) {
                strSegons = "0" + segons;
            }
            String temps = minuts + ":" + strSegons;
            escritor.write(temps);
            escritor.newLine();
            //Tancam fitxer
            escritor.close();

            //Afegim la cançó al arraylist:
            Canso c = new Canso(nomFitxer, titol, grup, album, any, temps);
            llistaCansons.add(c);

            //Actualitzam la taula de la vista:
            vista.actualitzarTaulaCansons(llistaCansons);
            
            vista.informarUsuari("Cançó '" + nomFitxer + "' afegida correctament");
        } else {
            vista.mostrarMissatgeAlerta("Primer has d'obrir una cançó");
        }
    }
    
    
    /**
     * Crea un arxiu de datapoint's únic que es correspon amb els valors extrets
     * de la gravació.
     * @throws LineUnavailableException
     * @throws IOException 
     */
    private void gravar() throws LineUnavailableException, IOException {
        //Cada gravació crea un arxiu únic, li donam de nou el temps epoch per
        //a evitar coincidencies entre gravacions:
        nomArxiuDarreraGravacio = ""+System.currentTimeMillis();
        insertarCansoMicro(nomArxiuDarreraGravacio, nomArxiuDarreraGravacio+".txt", DURACIO_GRAVACIO);
        vista.informarUsuari("Fi de la gravació");
    }
    
    
    /**
     * Codifica la cançó donada per poder afegir-la al fitxer de datapoint's
     * @param titol
     * @param fitxerWAV
     * @param fitxerSortida
     * @throws LineUnavailableException
     * @throws IOException
     * @throws UnsupportedAudioFileException 
     */
    private void insertarCansoFitxer(String titol, String fitxerWAV, String fitxerSortida) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        new Discretizador().transformarFitxer(fitxerWAV, titol, fitxerSortida);
    }
    
    
    /**
     * Indica si 2 cançons donades són iguals
     * @param c
     * @param cAux
     * @return true si s'ha trobat una coincidencia
     */
    public boolean sonIguals(Canso c, Canso cAux) {
        return c.getNom().equalsIgnoreCase(cAux.getNom())
                && c.getGrup().equalsIgnoreCase(cAux.getGrup())
                && c.getAlbum().equalsIgnoreCase(cAux.getAlbum())
                && c.getAny().equalsIgnoreCase(cAux.getAny())
                && c.getDuracio().equalsIgnoreCase(cAux.getDuracio());
    }
    
    
    /**
     * Inicialitzam la llista de cançons (les llegim del fitxer que conté la
     * seva info detallada, llegida anteriorment de la metadata dels seus
     * respectius fitxers)
     * @param path 
     * @return llista cançons
     */
    public ArrayList<Canso> llegirLlistaCansons(String path) {
        
        //Llegim el fitxer amb la info de cançons
        try {
            File arxiu = new File(path);
            BufferedReader lector = new BufferedReader(new FileReader(arxiu));
            String lectura = lector.readLine();
            while (lectura != null) {
                String[] dades = {"","","","",""};
                for (int i = 0; i < 5; i++) {
                    dades[i] = lector.readLine();
                }
                if (!lectura.equals("borrada")) {//conveni
                    llistaCansons.add(new Canso(lectura,dades[0],dades[1],dades[2],dades[3],dades[4]));
                }
                lectura = lector.readLine();
            }
            lector.close();
        } catch (IOException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return llistaCansons;
    }
    
    
    /**
     * Crea un arxiu a partir de la gravació
     * @param titol
     * @param fitxerSortida
     * @param segonsGravacio
     * @throws LineUnavailableException
     * @throws IOException 
     */
    private void insertarCansoMicro(String titol, String fitxerSortida, int segonsGravacio) throws LineUnavailableException, IOException {
        new Discretizador().transformarGravacio(new Gravadora(vista.getLabelGrafics()).gravar(segonsGravacio), titol, fitxerSortida);
    }
    
    
    /**
     * Compara els datapoint's de la BBDD amb una gravació feta per l'usuari
     * o bé amb la cançó que l'usuari té oberta en aquell moment
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private void comparar() throws FileNotFoundException, IOException {
        if (vista.compararGravacio()) {//hem de comparar una gravació de l'usuari
            if (nomArxiuDarreraGravacio != null) {
                Matcher.cercaCoincidencies(this, "BBDD.txt",nomArxiuDarreraGravacio + ".txt");
            } else {
                vista.mostrarMissatgeAlerta("Abans has de fer una gravació");
            }
        } else {//hem de comparar la cançó oberta actualment
            if (cansoOberta) {
                try {
                    //Cercam coincidencies amb la cançó oberta actualment però abans l'hem de codificar
                    String nom = fitxerAudio.getName();
                    String nomfitxer;
                    if (nom.endsWith(".mp3")) {
                         nomfitxer = nom.replace(".mp3", ".txt");                        
                    } else if(nom.endsWith("wav")) {
                        nomfitxer = nom.replace(".wav", ".txt");                        
                    } else {
                        nomfitxer = nom;
                    }
                    insertarCansoFitxer(nom, fitxerAudio.getAbsolutePath(), nomfitxer);
                    
                    Matcher.cercaCoincidencies(this, "BBDD.txt",nomfitxer);
                } catch (LineUnavailableException ex) {
                    Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedAudioFileException ex) {
                    Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            } else {
                vista.mostrarMissatgeAlerta("Abans has d'obrir una cançó");
            }
        }
    }
    
    

    public void esborraLinea(String basedades, String lineaEsborrar) {
        try {
            File fitxer = new File(basedades);
            if (!fitxer.isFile()) {
                System.out.println("Parameter is not an existing file");
                return;
            }

            //Construct the new file that will later be renamed to the original filename.
            File tempFile = new File(fitxer.getAbsolutePath() + ".tmp");

            BufferedReader br = new BufferedReader(new FileReader(basedades));
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
            String line = null;

            //Read from the original file and write to the new
            //unless content matches data to be removed.
            while ((line = br.readLine()) != null) {

                if (!line.trim().equals(lineaEsborrar)) {

                    pw.println(line);
                    pw.flush();
                } else {
                    pw.println("borrada");
                }
            }
            pw.close();
            br.close();

            //Delete the original file
            if (!fitxer.delete()) {
                System.out.println("Could not delete file");
                return;
            }

            //Rename the new file to the filename the original file had.
            if (!tempFile.renameTo(fitxer)) {
                System.out.println("Could not rename file");
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    /**
     * Marca com a brossa una cançó
     * @param basedades
     * @param canso 
     */
    public void borrarCanso(String basedades, String canso) {
        this.esborraLinea(basedades, canso);
    }
    
    
    /**
     * Mètode intermig per a treure informació per la consola de l'aplicació
     * @param m 
     */
    public void informarUsuari(String m) {
        vista.informarUsuari(m);
    }
    
    
}