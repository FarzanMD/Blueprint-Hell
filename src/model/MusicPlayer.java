package model;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MusicPlayer {
    private static MusicPlayer INSTANCE;
    private final Clip clip;
    private final FloatControl volumeControl;
    private boolean selectStop = false;

    private MusicPlayer() {
        Clip tmpClip = null;
        FloatControl tmpControl = null;
        try {
            File audioFile = new File("src/assets/theme.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);

            // 1) Open a local Clip
            Clip clipForListener = AudioSystem.getClip();
            clipForListener.open(audioStream);

            // 2) Grab volume control if available
            FloatControl control = null;
            if (clipForListener.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                control = (FloatControl) clipForListener.getControl(FloatControl.Type.MASTER_GAIN);
            }

            // 3) Add listener using the final local clipForListener
            clipForListener.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && !selectStop) {
                    clipForListener.setFramePosition(0);
                    clipForListener.start();
                }
            });

            // 4) Assign to tmp variables
            tmpClip = clipForListener;
            tmpControl = control;

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

        // 5) Finally assign to the instance fields
        this.clip = tmpClip;
        this.volumeControl = tmpControl;
    }

    public static MusicPlayer getInstance() {
        if (INSTANCE == null) INSTANCE = new MusicPlayer();
        return INSTANCE;
    }

    public void play() {
        if (clip != null && !clip.isRunning()) {
            selectStop = false;
            clip.start();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            selectStop = true;
            clip.stop();
        }
    }

    /**
     * Set volume as percentage [0..100].
     * Internally maps to the gain range of the MASTER_GAIN control.
     */
    public void setVolume(int percent) {
        if (volumeControl == null) return;
        percent = Math.max(0, Math.min(100, percent));
        float min = volumeControl.getMinimum();   // typically -80 dB
        float max = volumeControl.getMaximum();   // typically 6 dB
        // linear interpolation in dB:
        float gain = min + (max - min) * (percent / 100f);
        volumeControl.setValue(gain);
    }

    /**
     * Return current volume as a percentage [0..100].
     */
    public int getVolume() {
        if (volumeControl == null) return 100;
        float min = volumeControl.getMinimum();
        float max = volumeControl.getMaximum();
        float gain = volumeControl.getValue();
        return Math.round((gain - min) / (max - min) * 100f);
    }
}
