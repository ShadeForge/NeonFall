// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Resources.Sounds;

import NeonFall.Manager.SoundManager;
import java.util.Random;
import javax.sound.sampled.LineEvent;
import NeonFall.Manager.ResourceManager;
import java.util.ArrayList;
import javax.sound.sampled.LineListener;

public class MusicPlayer implements LineListener
{
    private ArrayList<LineListener> listeners;
    private SpectrumSoundThread spectrumSoundThread;
    private ArrayList<String> musicList;
    private int currentPosition;
    private boolean isRandom;
    private boolean stopped;
    
    public MusicPlayer() {
        this.currentPosition = 0;
        this.isRandom = true;
        this.stopped = false;
        this.listeners = new ArrayList<LineListener>();
        this.musicList = ResourceManager.getMusicFiles();
        this.listeners.add(this);
    }
    
    @Override
    public void update(final LineEvent event) {
        final LineEvent.Type type = event.getType();
        if (type.equals(LineEvent.Type.CLOSE)) {
            if (!this.stopped) {
                this.next();
            }
            this.stopped = false;
        }
    }
    
    public void play() {
        if (this.isRandom) {
            this.currentPosition = new Random().nextInt(this.musicList.size());
        }
        final int id = SoundManager.playSpectrumSound(this.musicList.get(this.currentPosition), this.listeners, false);
        this.spectrumSoundThread = (SpectrumSoundThread)SoundManager.getSoundThread(id);
    }
    
    public void stop() {
        this.stopped = true;
        this.destroy();
    }
    
    public void next() {
        this.destroy();
        if (this.isRandom) {
            this.currentPosition = new Random().nextInt(this.musicList.size());
        }
        else {
            ++this.currentPosition;
            if (this.currentPosition == this.musicList.size()) {
                this.currentPosition = 0;
            }
        }
        final int id = SoundManager.playSpectrumSound(this.musicList.get(this.currentPosition), this.listeners, false);
        this.spectrumSoundThread = (SpectrumSoundThread)SoundManager.getSoundThread(id);
    }
    
    public void previous() {
        this.destroy();
        if (this.isRandom) {
            this.currentPosition = new Random().nextInt(this.musicList.size());
        }
        else {
            --this.currentPosition;
            if (this.currentPosition == -1) {
                this.currentPosition = this.musicList.size() - 1;
            }
        }
        final int id = SoundManager.playSpectrumSound(this.musicList.get(this.currentPosition), this.listeners, false);
        this.spectrumSoundThread = (SpectrumSoundThread)SoundManager.getSoundThread(id);
    }
    
    public void addListener(final SpectrumSoundListener listener) {
        this.listeners.add(listener);
        if (this.spectrumSoundThread != null) {
            this.spectrumSoundThread.addListener(listener);
        }
    }
    
    public void removeListener(final SpectrumSoundListener listener) {
        this.listeners.remove(listener);
        if (this.spectrumSoundThread != null) {
            this.spectrumSoundThread.removeListener(listener);
        }
    }
    
    public void destroy() {
        if (this.spectrumSoundThread != null) {
            this.spectrumSoundThread.stop();
        }
        this.spectrumSoundThread = null;
    }
}
