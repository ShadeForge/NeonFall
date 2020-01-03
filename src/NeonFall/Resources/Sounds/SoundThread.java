package NeonFall.Resources.Sounds;

import NeonFall.Manager.SoundManager;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Usage:
 * Author: lbald
 * Last Update: 08.01.2016
 */
public class SoundThread implements Runnable {

    public static final int PLAY = 1;
    public static final int REPEAT = 2;
    public static final int STOP = 3;
    protected static final int READ_BUFFER_SIZE = 4 * 1024;

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

    public SoundThread(File audioSource, int state, int id) {
        this.audioSource = audioSource;
        this.state = state;
        this.id = id;
    }

    protected void init() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        destroy();
        audioInputStream = AudioSystem.getAudioInputStream(audioSource);
        audioDataLength = READ_BUFFER_SIZE;
        audioDataBuffer = ByteBuffer.allocate(audioDataLength);
        audioDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
        nBytesRead = 0;

        if(sourceDataLine != null) {
            destroy();
        }
        AudioFormat audioFormat = audioInputStream.getFormat();
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                audioFormat.getSampleRate(), 16, audioFormat.getChannels(),
                audioFormat.getChannels()*2, audioFormat.getSampleRate(), false);
        audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);

        DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(lineInfo);
        int bufferSize = sourceDataLine.getBufferSize();

        sourceDataLine.open(audioFormat, bufferSize);

        if (sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            gainControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
        }
        if (sourceDataLine.isControlSupported(BooleanControl.Type.MUTE)) {
            muteControl = (BooleanControl) sourceDataLine.getControl(BooleanControl.Type.MUTE);
        }
        sourceDataLine.start();
    }

    @Override
    public void run() {
        try {
            do {
                init();
                while ((state == REPEAT || state == PLAY) && nBytesRead != -1 && state != STOP) {
                    processSound();
                }
            } while(state == REPEAT && state != STOP);
            destroy();
        } catch (IOException | LineUnavailableException | UnsupportedAudioFileException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public synchronized void stop() {
        state = STOP;
    }

    protected void destroy() {
        if (sourceDataLine != null) {
            sourceDataLine.flush();
            sourceDataLine.stop();
            sourceDataLine.close();
            sourceDataLine = null;
        }

        if (audioInputStream != null) {
            try {
                audioInputStream.close();
                audioInputStream = null;
            } catch (IOException ex) {
                System.out.println("Can not close AudioInputStream.");
            }
        }

        SoundManager.destroySoundThread(id);
    }

    protected void processSound() throws IOException {
        int toRead = audioDataLength;
        int totalRead = 0;
        while (toRead > 0 && (nBytesRead = audioInputStream.read(audioDataBuffer.array(), totalRead, toRead)) != -1) {
            totalRead += nBytesRead;
            toRead -= nBytesRead;
        }
        if (totalRead > 0) {
            byte[] trimBuffer = audioDataBuffer.array();
            if (totalRead < trimBuffer.length) {
                trimBuffer = new byte[totalRead];
                System.arraycopy(audioDataBuffer.array(), 0, trimBuffer, 0, totalRead);
            }
            sourceDataLine.write(trimBuffer, 0, totalRead);
        }
    }

    public void setGain(float gain) {
        gainControl.setValue(Math.min(Math.max(gain, gainControl.getMaximum()), gainControl.getMinimum()));
    }

    public void setMute(boolean mute) {
        muteControl.setValue(mute);
    }
}
