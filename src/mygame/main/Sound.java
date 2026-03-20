package mygame.main;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

    Clip clip;
    private URL soundURL;
    private FloatControl gainControl;
    

    public void setFile(String path) {
        try {
            soundURL = getClass().getResource(path);
            if (soundURL == null) {
                System.out.println("Không tìm thấy file âm thanh: " + path);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL);
            clip = AudioSystem.getClip();
            clip.open(ais);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            if (clip != null) {
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        try {
            if (clip != null) {
                clip.stop();
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
        }
}

    public void reset() {
        try {
            if (clip != null) {
                clip.setFramePosition(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isLoaded() {
        return clip != null;
    }

   public void setVolume(float volume) {
        if (gainControl != null) {
            float min = gainControl.getMinimum(); // thường là -80
            float max = gainControl.getMaximum();

            if (volume < min) volume = min;
            if (volume > max) volume = max;

            gainControl.setValue(volume);
        }
    }
}