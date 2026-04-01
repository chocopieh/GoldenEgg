package mygame.main;

import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {

    private Clip clip;
    private URL soundURL;

    public void setFile(String path) {
        try {
            if (clip != null) {
                clip.stop();
                clip.close();
            }

            soundURL = getClass().getResource(path);

            if (soundURL == null) {
                System.out.println("Không tìm thấy file âm thanh: " + path);
                clip = null;
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL);
            clip = AudioSystem.getClip();
            clip.open(ais);
            ais.close();

            System.out.println("Đã load âm thanh: " + path);

        } catch (Exception e) {
            System.out.println("Lỗi load âm thanh: " + path);
            e.printStackTrace();
            clip = null;
        }
    }

    public void play() {
        try {
            if (clip != null) {
                clip.stop();
                clip.setFramePosition(0);
                clip.start();
                System.out.println("Đang phát âm thanh...");
            } else {
                System.out.println("Clip đang null, không phát được.");
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
                System.out.println("Đang lặp âm thanh...");
            }
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

    public boolean isRunning() {
        return clip != null && clip.isRunning();
    }

    public void setVolume(int volumePercent) {
        try {
            if (clip == null) return;
            if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return;

            FloatControl gainControl =
                    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);

            if (volumePercent <= 0) {
                gainControl.setValue(gainControl.getMinimum());
                return;
            }

            float volume = volumePercent / 100f;
            float dB = (float) (20.0 * Math.log10(volume));

            if (dB < gainControl.getMinimum()) dB = gainControl.getMinimum();
            if (dB > gainControl.getMaximum()) dB = gainControl.getMaximum();

            gainControl.setValue(dB);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}