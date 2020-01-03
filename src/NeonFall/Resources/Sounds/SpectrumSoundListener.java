// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Resources.Sounds;

import java.util.List;
import java.util.ArrayList;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineEvent;
import java.util.Iterator;
import java.util.LinkedList;
import com.sun.media.sound.FFT;
import java.nio.ByteBuffer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.LineListener;

public class SpectrumSoundListener implements LineListener, Runnable
{
    private static final int MAX_LAST_VALUES = 16;
    private static final float TRANS_SPEED = 20.0f;
    public static final int DEFAULT_BLOCK_LENGTH = 8192;
    public static final float DEFAULT_LINEAR_BIN_GAIN = 2.0f;
    public static final float DEFAULT_SPECTRUM_ANALYSER_GAIN = 0.001f;
    public static final int DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT = 32;
    private SourceDataLine sourceDataLine;
    private ByteBuffer audioDataBuffer;
    private double[][] audioChannels;
    private int blockLength;
    private int channelCount;
    private int frameSize;
    private int sampleSizeInBits;
    private int channelSize;
    private float audioSampleSize;
    private Band[] bandTable;
    private double[] binGainTable;
    private int bandCount;
    private double gain;
    private FFT fft;
    private int fftWindowLength;
    private double[] freqTable;
    private float[] windowCoefficients;
    private float linearBinGain;
    private boolean isRunning;
    private LinkedList<Double> lastValues;
    private double strength;
    private double average;
    
    public SpectrumSoundListener() {
        this.blockLength = 8192;
        this.strength = 0.0;
        this.average = 0.0;
    }
    
    @Override
    public void run() {
        this.isRunning = true;
        int bottomBinNum = 0;
        while (this.isRunning) {
            final double[] channelSamples = this.averageChannels(this.audioChannels);
            this.applyWindow(this.fftWindowLength, channelSamples);
            this.extractData();
            this.fft.transform(channelSamples);
            synchronized (this) {
                for (int bandNum = 0; bandNum < this.bandCount; ++bandNum) {
                    final int topBinNum = this.bandTable[bandNum].distribution;
                    double tempDouble = 0.0;
                    int tempInt = 0;
                    for (int binNum = bottomBinNum; binNum < topBinNum; ++binNum) {
                        final double binMag = channelSamples[binNum];
                        if (binMag > tempDouble) {
                            tempDouble = binMag;
                            tempInt = binNum;
                        }
                    }
                    bottomBinNum = topBinNum;
                    final double bandMag = tempDouble * this.binGainTable[tempInt] * this.gain;
                    this.freqTable[bandNum] = bandMag;
                }
                this.updateLastValues();
                this.strength = this.lastValues.getFirst();
                this.average = 0.0;
                for (final Double lastValue : this.lastValues) {
                    this.average += lastValue;
                }
                this.average /= 16.0;
            }
        }
    }
    
    private void updateLastValues() {
        final double[] values = this.getFreqTable();
        double testValue = 0.0;
        if (values != null) {
            for (final double value : values) {
                testValue += value;
            }
            if (testValue != this.lastValues.getFirst()) {
                this.lastValues.addFirst(testValue);
                if (this.lastValues.size() > 16) {
                    this.lastValues.removeLast();
                }
            }
        }
    }
    
    @Override
    public void update(final LineEvent event) {
        final LineEvent.Type type = event.getType();
        if (type.equals(LineEvent.Type.OPEN)) {
            this.open((SourceDataLine)event.getLine());
        }
        else if (type.equals(LineEvent.Type.START)) {
            this.start();
        }
        else if (type.equals(LineEvent.Type.STOP)) {
            this.stop();
        }
        else if (type.equals(LineEvent.Type.CLOSE)) {
            this.close();
        }
    }
    
    private void open(final SourceDataLine sourceDataLine) {
        this.sourceDataLine = sourceDataLine;
        this.bandCount = 32;
        final AudioFormat audioFormat = sourceDataLine.getFormat();
        this.audioChannels = new double[2][this.blockLength];
        this.fftWindowLength = Math.min(this.blockLength, 2048);
        final float fftSampleRate = sourceDataLine.getFormat().getFrameRate();
        this.linearBinGain = 2.0f;
        this.gain = 0.0010000000474974513;
        final int binCount = this.fftWindowLength >> 1;
        this.channelCount = audioFormat.getChannels();
        this.frameSize = audioFormat.getFrameSize();
        this.sampleSizeInBits = audioFormat.getSampleSizeInBits();
        this.channelSize = this.frameSize / this.channelCount;
        this.audioSampleSize = (float)(1 << this.sampleSizeInBits - 1);
        this.audioDataBuffer = ByteBuffer.allocate(sourceDataLine.getBufferSize());
        this.calculateWindowCoefficients(this.fftWindowLength);
        (this.lastValues = new LinkedList<Double>()).add(0.0);
        this.freqTable = new double[this.bandCount];
        this.bandTable = this.createLogBandDistribution(this.bandCount, binCount);
        this.binGainTable = this.createBinGainTable(binCount, fftSampleRate);
        this.fft = new FFT(this.fftWindowLength, 1);
    }
    
    public Band[] createLogBandDistribution(final int bandCount, final int binCount) {
        final int sso = 2;
        final double lso = 20.0;
        final int hss = binCount - 2;
        final double o = Math.log(20.0);
        final double r = (bandCount - 1) / (Math.log(hss + 20.0) - o);
        int lcb = 1;
        final List<Band> bands = new ArrayList<Band>();
        bands.add(new Band(2));
        for (int b = 0; b < hss; ++b) {
            final double cb = (Math.log(b + 20.0) - o) * r + 1.0;
            if (Math.round(cb) != lcb) {
                bands.add(new Band(b + 2));
                lcb = (int)Math.round(cb);
            }
        }
        if (bands.size() < bandCount) {
            bands.add(new Band(hss - 1 + 2));
        }
        return bands.toArray(new Band[bands.size()]);
    }
    
    public double[] createBinGainTable(final int binCount, final float sampleRate) {
        final float[] fqt = calculateFrequencyTable(binCount, sampleRate);
        this.binGainTable = new double[binCount];
        for (int i = 0; i < binCount; ++i) {
            this.binGainTable[i] = (fqt[i] / this.linearBinGain + 512.0f) / 512.0f * (this.linearBinGain * 1.5f);
        }
        return this.binGainTable;
    }
    
    public static float[] calculateFrequencyTable(final int spectrumLength, final float sampleRate) {
        final float maxFreq = sampleRate / 2.0f;
        final float binWidth = maxFreq / spectrumLength;
        final float[] freqTable = new float[spectrumLength];
        int bin = 0;
        for (float binFreq = binWidth; binFreq <= maxFreq; binFreq += binWidth) {
            freqTable[bin] = binFreq;
            ++bin;
        }
        return freqTable;
    }
    
    public void calculateWindowCoefficients(final int windowLength) {
        if (this.windowCoefficients == null || this.windowCoefficients.length != windowLength) {
            this.windowCoefficients = new float[windowLength];
            for (int k = 0; k < windowLength; ++k) {
                this.windowCoefficients[k] = (float)(0.54 - 0.46 * Math.cos(6.283185307179586 * k / windowLength));
            }
        }
    }
    
    public void applyWindow(final int fftWindowLength, final double[] audioChannels) {
        for (int i = 0; i < fftWindowLength; ++i) {
            final double tempDouble = audioChannels[i] * this.windowCoefficients[i];
            audioChannels[i] = tempDouble;
        }
    }
    
    public void extractData() {
        final long lfp = this.sourceDataLine.getLongFramePosition();
        synchronized (this.audioDataBuffer) {
            final int offset = (int)(lfp * this.frameSize % this.audioDataBuffer.capacity());
            for (int sampleNum = 0, position = offset; sampleNum < this.blockLength; ++sampleNum, position += this.frameSize) {
                if (position >= this.audioDataBuffer.capacity()) {
                    position = 0;
                }
                for (int channelNum = 0, cdp = 0; channelNum < this.channelCount; ++channelNum, cdp += this.channelSize) {
                    float signMask = (this.audioDataBuffer.get(position + cdp) & 0xFF) - 128.0f;
                    int bit = 8;
                    int bytePos = 1;
                    while (bit < this.sampleSizeInBits) {
                        signMask += this.audioDataBuffer.get(position + cdp + bytePos) << bit;
                        ++bytePos;
                        bit += 8;
                    }
                    this.audioChannels[channelNum][sampleNum] = signMask / this.audioSampleSize;
                }
            }
        }
        if (this.channelCount == 1) {
            for (int sampleNum = 0; sampleNum < this.blockLength; ++sampleNum) {
                this.audioChannels[1][sampleNum] = this.audioChannels[0][sampleNum];
            }
        }
    }
    
    public double[] averageChannels(final double[][] audioChannels) {
        final int length = audioChannels[0].length;
        final int channelCount = audioChannels.length;
        final double[] outputSamples = new double[length];
        for (int sampleNum = 0; sampleNum < length; ++sampleNum) {
            float sum = 0.0f;
            for (final double[] audioChannel : audioChannels) {
                sum += (float)audioChannel[sampleNum];
            }
            outputSamples[sampleNum] = sum / channelCount;
        }
        return outputSamples;
    }
    
    public void writeAudioData(final byte[] audioData, final int offset, final int length) {
        synchronized (this.audioDataBuffer) {
            if (this.audioDataBuffer == null) {
                return;
            }
            if (this.audioDataBuffer.remaining() < length) {
                this.audioDataBuffer.clear();
            }
            this.audioDataBuffer.put(audioData, offset, length);
        }
    }
    
    public synchronized double[] getFreqTable() {
        return this.freqTable;
    }
    
    private void start() {
        new Thread(this).start();
    }
    
    private void stop() {
        this.isRunning = false;
    }
    
    private void close() {
        this.isRunning = false;
        if (this.audioDataBuffer != null) {
            this.audioDataBuffer.clear();
        }
    }
    
    public synchronized float getStrenghest() {
        return (float)this.strength;
    }
    
    public synchronized float getAverage() {
        return (float)this.average;
    }
    
    private class Band
    {
        private int distribution;
        
        private Band(final int distribution) {
            this.distribution = distribution;
        }
    }
}
