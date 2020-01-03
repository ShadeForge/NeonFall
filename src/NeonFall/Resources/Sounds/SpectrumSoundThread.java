package NeonFall.Resources.Sounds;

import javax.sound.sampled.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

/**
 * Usage:
 * Author: lbald
 * Last Update: 08.01.2016
 */
public class SpectrumSoundThread extends SoundThread {

    private final ArrayList<LineListener> listeners;

    public SpectrumSoundThread(SoundThread thread, ArrayList<LineListener> listeners, int id) {
        super(thread.audioSource, thread.state, id);
        this.listeners = listeners;
    }

    @Override
    protected void init() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        destroy();
        audioInputStream = AudioSystem.getAudioInputStream(audioSource);
        audioDataLength = READ_BUFFER_SIZE;
        audioDataBuffer = ByteBuffer.allocate(audioDataLength);
        audioDataBuffer.order(ByteOrder.LITTLE_ENDIAN);
        nBytesRead = 0;

        AudioFormat audioFormat = audioInputStream.getFormat();
        audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                audioFormat.getSampleRate(), 16, audioFormat.getChannels(),
                audioFormat.getChannels()*2, audioFormat.getSampleRate(), false);
        audioInputStream = AudioSystem.getAudioInputStream(audioFormat, audioInputStream);

        DataLine.Info lineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
        sourceDataLine = (SourceDataLine) AudioSystem.getLine(lineInfo);
        int bufferSize = sourceDataLine.getBufferSize();

        listeners.forEach(sourceDataLine::addLineListener);
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

            synchronized (listeners) {
                sourceDataLine.write(trimBuffer, 0, totalRead);
                for (LineListener listener : listeners) {
                    if(listener instanceof SpectrumSoundListener)
                        ((SpectrumSoundListener)listener).writeAudioData(trimBuffer, 0, totalRead);
                }
            }
        }
    }

    public synchronized void addListener(SpectrumSoundListener listener) {
        listeners.add(listener);
        sourceDataLine.addLineListener(listener);
    }

    public synchronized void removeListener(SpectrumSoundListener listener) {
        listeners.remove(listener);
        sourceDataLine.removeLineListener(listener);
    }
}
