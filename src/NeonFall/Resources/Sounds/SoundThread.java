// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Resources.Sounds;

import NeonFall.Manager.SoundManager;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioFormat;
import java.nio.ByteOrder;
import javax.sound.sampled.AudioSystem;
import java.nio.ByteBuffer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;

public class SoundThread implements Runnable
{
    public static final int PLAY = 1;
    public static final int REPEAT = 2;
    public static final int STOP = 3;
    protected static final int READ_BUFFER_SIZE = 4096;
    protected FloatControl gainControl;
    protected BooleanControl muteControl;
    protected File audioSource;
    protected int state;
    protected AudioInputStream audioInputStream;
    protected SourceDataLine sourceDataLine;
    protected int audioDataLength;
    protected ByteBuffer audioDataBuffer;
    protected int nBytesRead;
    protected int id;
    
    public SoundThread(final File audioSource, final int state, final int id) {
        this.audioSource = audioSource;
        this.state = state;
        this.id = id;
    }
    
    protected void init() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this.destroy();
        this.audioInputStream = AudioSystem.getAudioInputStream(this.audioSource);
        this.audioDataLength = 4096;
        (this.audioDataBuffer = ByteBuffer.allocate(this.audioDataLength)).order(ByteOrder.LITTLE_ENDIAN);
        this.nBytesRead = 0;
        if (this.sourceDataLine != null) {
            this.destroy();
        }
        AudioFormat audioFormat = this.audioInputStream.getFormat();
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, audioFormat.getSampleRate(), 16, audioFormat.getChannels(), audioFormat.getChannels() * 2, audioFormat.getSampleRate(), false);
        this.audioInputStream = AudioSystem.getAudioInputStream(audioFormat, this.audioInputStream);
        final DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, -1);
        this.sourceDataLine = (SourceDataLine)AudioSystem.getLine(lineInfo);
        final int bufferSize = this.sourceDataLine.getBufferSize();
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
    public void run() {
        try {
            do {
                this.init();
                while ((this.state == 2 || this.state == 1) && this.nBytesRead != -1 && this.state != 3) {
                    this.processSound();
                }
            } while (this.state == 2 && this.state != 3);
            this.destroy();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public synchronized void stop() {
        this.state = 3;
    }
    
    protected void destroy() {
        if (this.sourceDataLine != null) {
            this.sourceDataLine.flush();
            this.sourceDataLine.stop();
            this.sourceDataLine.close();
            this.sourceDataLine = null;
        }
        if (this.audioInputStream != null) {
            try {
                this.audioInputStream.close();
                this.audioInputStream = null;
            }
            catch (IOException ex) {
                System.out.println("Can not close AudioInputStream.");
            }
        }
        SoundManager.destroySoundThread(this.id);
    }
    
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
            this.sourceDataLine.write(trimBuffer, 0, totalRead);
        }
    }
    
    public void setGain(final float gain) {
        this.gainControl.setValue(Math.min(Math.max(gain, this.gainControl.getMaximum()), this.gainControl.getMinimum()));
    }
    
    public void setMute(final boolean mute) {
        this.muteControl.setValue(mute);
    }
}
