// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Resources.Sounds;

import java.io.File;

public class Sound
{
    private File audioSource;
    
    public Sound(final String path) {
        this.audioSource = new File(path);
    }
    
    public SoundThread playSound(final int state, final int id) {
        return new SoundThread(this.audioSource, state, id);
    }
    
    public static Sound loadSoundFromFile(final String path) {
        return new Sound(path);
    }
}
