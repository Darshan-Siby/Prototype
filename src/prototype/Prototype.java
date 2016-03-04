/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package prototype;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

/**
 *
 * @author Kuncheria
 */
public class Prototype {

    Player player;
    FileInputStream fis;
    BufferedInputStream bis;
    long PauseLocation;
    long SongLength;
    String FileLocation;
    boolean control = true;

    public static void main(String[] args) {
        // TODO code application logic here
        NewJFrame jf = new NewJFrame();
        jf.setVisible(true);
    }

    public void play(String path) {
        try {
            fis = new FileInputStream(path);
            bis = new BufferedInputStream(fis);
            player = new Player(bis);
            SongLength = fis.available();
            FileLocation = path + "";
        } catch (IOException ex) {
            Logger.getLogger(Prototype.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JavaLayerException ex) {
            System.err.println("CANNOT ASSIGN PLAYER");
        }
        control = true;
        new Thread() {

            @Override
            public void run() {
                try {
                    player.play();
                } catch (JavaLayerException ex) {
                    System.err.println("PLAYER CANNOT PLAY");
                }
            }
        }.start();
    }

    public void stop() {
        control = false;
        if (player != null) {
            player.close();
            PauseLocation = SongLength;
        }
    }

    public void pause() {
        control = false;
        if (player != null) {
            try {
                PauseLocation = fis.available();
                player.close();
            } catch (IOException ex) {
                Logger.getLogger(Prototype.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void resume() {
        if (control == false) {
            try {
                fis = new FileInputStream(FileLocation);
                bis = new BufferedInputStream(fis);
                player = new Player(bis);
                fis.skip(SongLength - PauseLocation);
            } catch (IOException ex) {
                Logger.getLogger(Prototype.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JavaLayerException ex) {
                System.err.println("CANNOT ASSIGN PLAYER");
            }
            control = true;
            new Thread() {

                @Override
                public void run() {
                    try {
                        player.play();
                    } catch (JavaLayerException ex) {
                        System.err.println("PLAYER CANNOT PLAY");
                    }
                }
            }.start();

        }
    }

    public void volChange(float vl) {
        try {
            Mixer.Info[] infos = AudioSystem.getMixerInfo();
            for (Mixer.Info info : infos) {
                Mixer mixer = AudioSystem.getMixer(info);
                if (mixer.isLineSupported(Port.Info.SPEAKER)) {
                    Port port = (Port) mixer.getLine(Port.Info.SPEAKER);
                    port.open();
                    if (port.isControlSupported(FloatControl.Type.VOLUME)) {
                        FloatControl volume = (FloatControl) port.getControl(FloatControl.Type.VOLUME);
                        vl = vl / 100;
                        volume.setValue(vl);
                    }
                    port.close();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro\n" + e);
        }
    }
    
    public void slider(JProgressBar jb){
        float pos;
        int intpos;
        if(fis!=null){
        try {
            pos= (SongLength-fis.available());
            pos=((pos/SongLength)*200);
            intpos=(int) pos;
            jb.setValue(intpos);
        } catch (IOException ex) {
            Logger.getLogger(Prototype.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    }
    
    public void seekTo(int val){
            try {
                fis = new FileInputStream(FileLocation);
                bis = new BufferedInputStream(fis);
                player = new Player(bis);
                float floatval=(float)val;
                floatval=(float) (floatval/200.0);
                floatval=(floatval*SongLength);
                fis.skip((long)floatval);
                
            } catch (IOException ex) {
                Logger.getLogger(Prototype.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JavaLayerException ex) {
                System.err.println("CANNOT ASSIGN PLAYER");
            }
            control = true;
            new Thread() {

                @Override
                public void run() {
                    try {
                        player.play();
                    } catch (JavaLayerException ex) {
                        System.err.println("PLAYER CANNOT PLAY");
                    }
                }
            }.start();

        
    }
}
