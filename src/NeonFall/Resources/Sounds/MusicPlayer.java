package NeonFall.Resources.Sounds;

import NeonFall.Manager.ResourceManager;
import NeonFall.Manager.SoundManager;

import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.util.ArrayList;
import java.util.Random;

/**
 * Usage:
 * Author: lbald
 * Last Update: 11.01.2016
 */
public class MusicPlayer implements LineListener {

    private ArrayList<LineListener> listeners;
    private SpectrumSoundThread spectrumSoundThread;
    private ArrayList<String> musicList;
    private int currentPosition = 0;
    private boolean isRandom = true;
    private boolean stopped = false;

    public MusicPlayer() {
        listeners = new ArrayList<>();
        musicList = ResourceManager.getMusicFiles();
        listeners.add(this);
    }

    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();

        if (type.equals(LineEvent.Type.CLOSE)) {
            if(!stopped) {
                next();
            }
            stopped = false;
        }
    }

    public void play() {

        if(isRandom) {
            currentPosition = new Random().nextInt(musicList.size());
        }
        int id = SoundManager.playSpectrumSound(musicList.get(currentPosition), listeners, false);
        spectrumSoundThread = (SpectrumSoundThread)SoundManager.getSoundThread(id);
    }

    public void stop() {
        stopped = true;
        destroy();
    }

    public void next() {
        destroy();

        if(isRandom) {
            currentPosition = new Random().nextInt(musicList.size());
        } else {
            currentPosition++;
            if (currentPosition == musicList.size())
                currentPosition = 0;
        }

        int id = SoundManager.playSpectrumSound(musicList.get(currentPosition), listeners, false);
        spectrumSoundThread = (SpectrumSoundThread)SoundManager.getSoundThread(id);
    }

    public void previous() {
        destroy();

        if(isRandom) {
            currentPosition = new Random().nextInt(musicList.size());
        } else {
            currentPosition--;
            if (currentPosition == -1)
                currentPosition = musicList.size() - 1;
        }

        int id = SoundManager.playSpectrumSound(musicList.get(currentPosition), listeners, false);
        spectrumSoundThread = (SpectrumSoundThread)SoundManager.getSoundThread(id);
    }

    public void addListener(SpectrumSoundListener listener) {
        listeners.add(listener);

        if(spectrumSoundThread != null)
            spectrumSoundThread.addListener(listener);
    }

    public void removeListener(SpectrumSoundListener listener) {
        listeners.remove(listener);

        if(spectrumSoundThread != null)
           spectrumSoundThread.removeListener(listener);
    }

    public void destroy() {
        if(spectrumSoundThread != null)
            spectrumSoundThread.stop();
        spectrumSoundThread = null;
    }
}
