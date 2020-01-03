// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Manager;

import NeonFall.Resources.Sounds.SpectrumSoundThread;
import javax.sound.sampled.LineListener;
import java.util.ArrayList;
import NeonFall.Resources.Sounds.MusicPlayer;
import NeonFall.Resources.Sounds.SoundThread;
import java.util.HashMap;

public class SoundManager
{
    private static HashMap<Integer, SoundThread> soundThreads;
    private static int currentID = -1;
    public static MusicPlayer musicPlayer;
    
    public static void init() {
        SoundManager.soundThreads = new HashMap<Integer, SoundThread>();
        SoundManager.musicPlayer = new MusicPlayer();
    }
    
    public static int playSound(final String path, final boolean repeat) {
        ++SoundManager.currentID;
        final SoundThread soundThread = ResourceManager.getSound(path).playSound(repeat ? 2 : 1, SoundManager.currentID);
        SoundManager.soundThreads.put(SoundManager.currentID, soundThread);
        new Thread(soundThread).start();
        return SoundManager.currentID;
    }
    
    public static int playSpectrumSound(final String path, final ArrayList<LineListener> listeners, final boolean repeat) {
        ++SoundManager.currentID;
        final SoundThread soundThread = ResourceManager.getSound(path).playSound(repeat ? 2 : 1, SoundManager.currentID);
        final SpectrumSoundThread spectrumSoundThread = new SpectrumSoundThread(soundThread, listeners, SoundManager.currentID);
        SoundManager.soundThreads.put(SoundManager.currentID, spectrumSoundThread);
        new Thread(spectrumSoundThread).start();
        return SoundManager.currentID;
    }
    
    public static int playMusic(final String path, final ArrayList<LineListener> listeners, final boolean repeat) {
        ++SoundManager.currentID;
        final SoundThread soundThread = ResourceManager.getSound(path).playSound(repeat ? 2 : 1, SoundManager.currentID);
        final SpectrumSoundThread spectrumSoundThread = new SpectrumSoundThread(soundThread, listeners, SoundManager.currentID);
        SoundManager.soundThreads.put(SoundManager.currentID, spectrumSoundThread);
        new Thread(spectrumSoundThread).start();
        return SoundManager.currentID;
    }
    
    public static void stopSound(final int ID) {
        SoundManager.soundThreads.get(ID).stop();
    }
    
    public static SoundThread getSoundThread(final int ID) {
        return SoundManager.soundThreads.get(ID);
    }
    
    public static void destroySoundThread(final int ID) {
        SoundManager.soundThreads.remove(ID);
    }
    
    public static void destroy() {
        SoundManager.soundThreads.values().forEach(SoundThread::stop);
        SoundManager.musicPlayer.destroy();
    }
}
