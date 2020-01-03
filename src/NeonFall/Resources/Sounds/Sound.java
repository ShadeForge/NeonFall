package NeonFall.Resources.Sounds;

import java.io.File;

/**
 * Usage:
 * Author: lbald
 * Last Update: 08.01.2016
 */
public class Sound {

    private File audioSource;

    public Sound(String path) {
        audioSource = new File(path);
    }

    public SoundThread playSound(int state, int id) {
        return new SoundThread(audioSource, state, id);
    }

    public static Sound loadSoundFromFile(String path) {
        return new Sound(path);
    }
}
