package NeonFall.Manager;

import NeonFall.Resources.Sounds.MusicPlayer;
import NeonFall.Resources.Sounds.SoundThread;
import NeonFall.Resources.Sounds.SpectrumSoundListener;
import NeonFall.Resources.Sounds.SpectrumSoundThread;

import javax.sound.sampled.LineListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Usage:
 * Author: lbald
 * Last Update: 08.01.2016
 */
public class SoundManager {

    private static HashMap<Integer, SoundThread> soundThreads;
    private static int currentID = -1;
    public static MusicPlayer musicPlayer;

    public static void init() {
        soundThreads = new HashMap<>();
        musicPlayer = new MusicPlayer();
    }

    public static int playSound(String path, boolean repeat) {
        currentID++;
        SoundThread soundThread = ResourceManager.getSound(path).playSound(repeat ? SoundThread.REPEAT : SoundThread.PLAY, currentID);
        soundThreads.put(currentID, soundThread);
        (new Thread(soundThread)).start();
        return currentID;
    }

    public static int playSpectrumSound(String path, ArrayList<LineListener> listeners, boolean repeat) {
        currentID++;
        SoundThread soundThread = ResourceManager.getSound(path).playSound(repeat ? SoundThread.REPEAT : SoundThread.PLAY, currentID);
        SpectrumSoundThread spectrumSoundThread = new SpectrumSoundThread(soundThread, listeners, currentID);
        soundThreads.put(currentID, spectrumSoundThread);
        (new Thread(spectrumSoundThread)).start();
        return currentID;
    }

    public static int playMusic(String path, ArrayList<LineListener> listeners, boolean repeat) {
        currentID++;
        SoundThread soundThread = ResourceManager.getSound(path).playSound(repeat ? SoundThread.REPEAT : SoundThread.PLAY, currentID);
        SpectrumSoundThread spectrumSoundThread = new SpectrumSoundThread(soundThread, listeners, currentID);
        soundThreads.put(currentID, spectrumSoundThread);
        (new Thread(spectrumSoundThread)).start();
        return currentID;
    }

    public static void stopSound(int ID) {
        soundThreads.get(ID).stop();
    }

    public static SoundThread getSoundThread(int ID) {
        return soundThreads.get(ID);
    }

    public static void destroySoundThread(int ID) {
        soundThreads.remove(ID);
    }

    public static void destroy() {
        soundThreads.values().forEach(SoundThread::stop);
        musicPlayer.destroy();
    }
}
