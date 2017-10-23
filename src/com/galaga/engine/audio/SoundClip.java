package com.galaga.engine.audio;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SoundClip {

    private Clip clip = null;
    private FloatControl gainControl;

    public SoundClip(String path)
    {
        try {
            InputStream audioSrc = SoundClip.class.getResourceAsStream(path);
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
            AudioFormat baseFormat = audioInputStream.getFormat();
            AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);
            AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat,audioInputStream);

            setClip(AudioSystem.getClip());
            getClip().open(dais);

            setGainControl((FloatControl)getClip().getControl(FloatControl.Type.MASTER_GAIN));

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

    }

    public void play()
    {
        if (clip == null){
            return;
        }

        stop();
        getClip().setFramePosition(0);
        while(!getClip().isRunning()){
            getClip().start();
        }
    }

    public void stop()
    {
        if (getClip().isRunning()) {
            getClip().stop();
        }
    }

    public FloatControl getGainControl() {
        return gainControl;
    }

    public void close()
    {
        stop();
        getClip().drain();
        getClip().close();
    }

    public void loop()
    {
        getClip().loop(Clip.LOOP_CONTINUOUSLY);
        play();
    }

    public void setVoulme(float value)
    {
        getGainControl().setValue(value);
    }

    public boolean isRunning()
    {
        return getClip().isRunning();
    }

    public void setGainControl(FloatControl gainControl) {
        this.gainControl = gainControl;
    }

    public Clip getClip() {
        return clip;
    }

    public void setClip(Clip clip) {
        this.clip = clip;
    }
}
