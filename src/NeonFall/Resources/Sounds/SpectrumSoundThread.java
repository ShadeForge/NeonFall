// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Resources.Sounds;

import java.util.Iterator;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.FloatControl;
import java.util.function.Consumer;
import javax.sound.sampled.Line;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineListener;
import java.util.ArrayList;

public class SpectrumSoundThread extends SoundThread
{
    private final ArrayList<LineListener> listeners;
    
    public SpectrumSoundThread(final SoundThread thread, final ArrayList<LineListener> listeners, final int id) {
        super(thread.audioSource, thread.state, id);
        this.listeners = listeners;
    }
    
    @Override
    protected void init() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this.destroy();
        this.audioInputStream = AudioSystem.getAudioInputStream(this.audioSource);
        this.audioDataLength = 4096;
        (this.audioDataBuffer = ByteBuffer.allocate(this.audioDataLength)).order(ByteOrder.LITTLE_ENDIAN);
        this.nBytesRead = 0;
        AudioFormat audioFormat = this.audioInputStream.getFormat();
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16, audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), false);
        this.audioInputStream = AudioSystem.getAudioInputStream(audioFormat, this.audioInputStream);
        final DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, -1);
        this.sourceDataLine = (SourceDataLine)AudioSystem.getLine(lineInfo);
        final int bufferSize = this.sourceDataLine.getBufferSize();
        this.listeners.forEach(this.sourceDataLine::addLineListener);
        this.sourceDataLine.open(audioFormat, bufferSize);
        if (this.sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            this.gainControl = (FloatControl)this.sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
        }
        if (this.sourceDataLine.isControlSupported(BooleanControl.Type.MUTE)) {
            this.muteControl = (BooleanControl)this.sourceDataLine.getControl(BooleanControl.Type.MUTE);
        }
        this.sourceDataLine.start();
    }
    
    @Override
    protected void processSound() throws IOException {
        int toRead;
        int totalRead;
        for (toRead = this.audioDataLength, totalRead = 0; toRead > 0 && (this.nBytesRead = this.audioInputStream.read(this.audioDataBuffer.array(), totalRead, toRead)) != -1; totalRead += this.nBytesRead, toRead -= this.nBytesRead) {}
        if (totalRead > 0) {
            byte[] trimBuffer = this.audioDataBuffer.array();
            if (totalRead < trimBuffer.length) {
                trimBuffer = new byte[totalRead];
                System.arraycopy(this.audioDataBuffer.array(), 0, trimBuffer, 0, totalRead);
            }
            synchronized (this.listeners) {
                this.sourceDataLine.write(trimBuffer, 0, totalRead);
                for (final LineListener listener : this.listeners) {
                    if (listener instanceof SpectrumSoundListener) {
                        ((SpectrumSoundListener)listener).writeAudioData(trimBuffer, 0, totalRead);
                    }
                }
            }
        }
    }
    
    public synchronized void addListener(final SpectrumSoundListener listener) {
        this.listeners.add(listener);
        this.sourceDataLine.addLineListener(listener);
    }
    
    public synchronized void removeListener(final SpectrumSoundListener listener) {
        this.listeners.remove(listener);
        this.sourceDataLine.removeLineListener(listener);
    }
}
