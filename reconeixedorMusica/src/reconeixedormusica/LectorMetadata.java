
package reconeixedormusica;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v2.APICID3V2Frame;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;
import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

/**
 * Llegeix la metadata d'arxius d'audio MP3. S'han usat dues classes diferents:
 * una per a llegir info del tipus nom de la cançó, artista... i una altra per 
 * llegir la portada. No sempre és possible llegir tags de metadata (veure
 * especificació ID3: http://www.id3.org/). En aquest cas es llegeix la portada
 * enmagatzemada en local (carpeta "portades" del projecte). Si tampoc hi és, es
 * mostra una caràtula per defecte.
 * @author Bernat i Joan
 */
public class LectorMetadata {
    
    private String nom;
    private String grup;
    private String album;
    private String genere;
    private String any;
    
    private AudioFileFormat baseFileFormat;
    private Map properties;
    private int duracio;
    
    private MediaFile mediaFile;
    private byte[] arrayByte;
    private ID3V2_3_0Tag tagImatge;
    private APICID3V2Frame frame[];
    
    
    /**
     * Llegeix la metadata d'un arxiu
     * @param file arxiu a llegir els seus tags
     */
    public LectorMetadata(File file) {
        try {
            MP3File mp3file = new MP3File(file);
            //TagOptionSingleton.getInstance().setDefaultSaveMode(TagConstant.MP3_FILE_SAVE_OVERWRITE);
            AbstractMP3Tag abstractTag = mp3file.getID3v2Tag();
            nom = abstractTag.getSongTitle();
            if (nom.equals("")) nom = "-";//conveni
            grup = abstractTag.getLeadArtist();
            if (grup.equals("")) grup = "-";
            album = abstractTag.getAlbumTitle();
            if (album.equals("")) album = "-";
            genere = abstractTag.getSongGenre();
            if (genere.equals("")) genere = "-";
            any = abstractTag.getYearReleased();
            if (any.equals("")) any = "-";
            
            //Format de la cançó i propietats:
            baseFileFormat = AudioSystem.getAudioFileFormat(file);
            properties = baseFileFormat.properties();
            //Duració de la cançó:
            long temp = (Long) properties.get("duration");
            duracio = (int)(temp / 1000000);//milisegons -> segons
            
            //Caràtula de l'arxiu:
            try {
                mediaFile = null;
                arrayByte = null;
                mediaFile = new org.blinkenlights.jid3.MP3File(file);//per evitar confusions

                if (mediaFile != null) {
                    for (Object obj : mediaFile.getTags()) {
                        if (obj  instanceof ID3V2_3_0Tag) {
                            tagImatge = (ID3V2_3_0Tag) obj;
                            if ((arrayByte == null) && (tagImatge.getAPICFrames() != null) && (tagImatge.getAPICFrames().length > 0)) {
                                //Pillam la primera imatge disponible:
                                frame = tagImatge.getAPICFrames();
                                for (int i = 0; i<tagImatge.getAPICFrames().length; i++){
                                    if (frame[i] != null){
                                        arrayByte = frame[i].getPictureData();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ID3Exception ex) {
                Logger.getLogger(LectorMetadata.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(LectorMetadata.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TagException ex) {
            Logger.getLogger(LectorMetadata.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(LectorMetadata.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(LectorMetadata.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            System.out.println("no es pot llegir metadata si no és mp3");
        }
        
    }
    
    
    /**
     * Torna la caràtula de l'arxiu d'audio (o null si no existeix o no
     * s'ha pogut llegir)
     * @return imatge portada
     */
    public Image getCaratula(){
        if (arrayByte != null){
            try {
                InputStream in = new ByteArrayInputStream(arrayByte);
                BufferedImage bImageFromConvert = ImageIO.read(in);
                if (bImageFromConvert != null) {
                    return Toolkit.getDefaultToolkit().createImage(bImageFromConvert.getSource());
                }
            } catch (IOException ex) {
                Logger.getLogger(LectorMetadata.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //Si no hem trobat la caràctula a la metainformació de l'arxiu
        //l'haurem de cercar en local (null és un conveni)
        return null;
    }
    
    
    /**
     * Torna el nom de la cançó
     * @return nom
     */
    public String getNom() {
        return nom;
    }

    
    /**
     * Torna el grup que ha fet la cançó
     * @return grup/artista
     */
    public String getGrup() {
        return grup;
    }

    
    /**
     * Torna l'àlbum al qual pertany la cançó
     * @return àlbum
     */
    public String getAlbum() {
        return album;
    }
    
    
    /**
     * Torna el gènere de la cançó
     * @return gènere
     */
    public String getGenere() {
        return genere;
    }
    
    
    /**
     * Torna l'any de la cançó
     * @return any de composició
     */
    public String getAny() {
        return any;
    }
    
    
    /**
     * Torna la duració de la cançó (en segons)
     * @return temps que dura la cançó
     */
    public int getDuracio() {
        return duracio;
    }
    
}