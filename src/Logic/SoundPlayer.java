package Logic;

import javax.sound.sampled.*;
import java.net.URL;
import java.io.File;


public class SoundPlayer {

    private Clip clip;

    public void playLoop(String path) {
        try {
            URL url = getClass().getResource(path);
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);

            clip = AudioSystem.getClip();
            clip.open(audio);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        // ---- NUEVO MÉTODO ----
    public void playOnce(String path) {
    try {
        URL url = getClass().getResource(path);
        if (url == null) {
            System.out.println("No se encontró el archivo: " + path);
            return;
        }
        AudioInputStream audio = AudioSystem.getAudioInputStream(url);
        Clip clip = AudioSystem.getClip();
        clip.open(audio);
        clip.start();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
}
