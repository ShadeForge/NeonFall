package NeonFall.Resources.Sounds;

import com.sun.media.sound.FFT;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Usage:
 * Author: lbald
 * Last Update: 09.01.2016
 */
public class SpectrumSoundListener implements LineListener, Runnable {

    private static final int MAX_LAST_VALUES = 16;
    private static final float TRANS_SPEED = 20f;

    private class Band {
        private int distribution;
        private Band(int distribution) {
            this.distribution = distribution;
        }
    }

    public static final int DEFAULT_BLOCK_LENGTH = 8192;
    public static final float DEFAULT_LINEAR_BIN_GAIN = 2.0F;
    public static final float DEFAULT_SPECTRUM_ANALYSER_GAIN = 0.001F;
    public static final int DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT = 32;

    private SourceDataLine sourceDataLine;
    private ByteBuffer audioDataBuffer;
    private double[][] audioChannels;
    private int blockLength = DEFAULT_BLOCK_LENGTH;
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
    private double strength = 0;
    private double average = 0;

    @Override
    public void run() {

        isRunning = true;

        int binNum, topBinNum;
        int bottomBinNum = 0;
        int tempInt;
        double tempDouble;
        double bandMag;

        while(isRunning) {
            double[] channelSamples = averageChannels(audioChannels);
            applyWindow(fftWindowLength, channelSamples);
            extractData();
            fft.transform(channelSamples);

            synchronized (this) {
                for (int bandNum = 0; bandNum < bandCount; bandNum++) {

                    topBinNum = bandTable[bandNum].distribution;
                    tempDouble = 0;
                    tempInt = 0;

                    for (binNum = bottomBinNum; binNum < topBinNum; binNum++) {
                        double binMag = channelSamples[binNum];
                        if (binMag > tempDouble) {
                            tempDouble = binMag;
                            tempInt = binNum;
                        }
                    }
                    bottomBinNum = topBinNum;

                    bandMag = (tempDouble * binGainTable[tempInt]) * gain;

                    freqTable[bandNum] = bandMag;
                }

                updateLastValues();
                strength = lastValues.getFirst();
                average = 0;
                for (Double lastValue : lastValues) {
                    average += lastValue;
                }
                average /= MAX_LAST_VALUES;
            }
        }
    }

    private void updateLastValues() {
        double[] values = getFreqTable();
        double testValue = 0;
        if (values != null) {
            for (double value : values)
                testValue += value;

            if(testValue!=lastValues.getFirst()) {
                lastValues.addFirst(testValue);

                if(lastValues.size() > MAX_LAST_VALUES) {
                    lastValues.removeLast();
                }
            }
        }
    }

    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();

        if (type.equals(LineEvent.Type.OPEN)) {
            open((SourceDataLine) event.getLine());
        } else if (type.equals(LineEvent.Type.START)) {
            start();
        } else if (type.equals(LineEvent.Type.STOP)) {
            stop();
        } else if (type.equals(LineEvent.Type.CLOSE)) {
            close();
        }
    }

    private void open(SourceDataLine sourceDataLine) {

        this.sourceDataLine = sourceDataLine;
        this.bandCount = DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT;
        AudioFormat audioFormat = sourceDataLine.getFormat();
        this.audioChannels = new double[2][blockLength];
        this.fftWindowLength = Math.min(blockLength, 2048); // If blockLength=8192 then fftWindowLength=2048
        float fftSampleRate = sourceDataLine.getFormat().getFrameRate();

        linearBinGain = DEFAULT_LINEAR_BIN_GAIN;
        gain = DEFAULT_SPECTRUM_ANALYSER_GAIN;
        int binCount = fftWindowLength >> 1;
        channelCount = audioFormat.getChannels();
        frameSize = audioFormat.getFrameSize();
        sampleSizeInBits = audioFormat.getSampleSizeInBits();
        channelSize = frameSize / channelCount;
        audioSampleSize = (1 << (sampleSizeInBits - 1));
        audioDataBuffer = ByteBuffer.allocate(sourceDataLine.getBufferSize());
        calculateWindowCoefficients(fftWindowLength);
        this.lastValues = new LinkedList<>();

        lastValues.add(0d);
        freqTable = new double[bandCount];
        bandTable = createLogBandDistribution(bandCount, binCount);
        binGainTable = createBinGainTable(binCount, fftSampleRate);

        fft = new FFT(fftWindowLength, 1);
    }

    public Band[] createLogBandDistribution(int bandCount, int binCount)
    {   final int       sso = 2;
        final double    lso = 20.0D;

        int hss = binCount - sso;

        double o = Math.log(lso);
        double r = (double) (bandCount - 1) / (Math.log(hss + lso) - o);

        int lcb = 1;

        List<Band> bands = new ArrayList<>();
        bands.add(new Band(sso));

        for (int b = 0; b < hss; b++) {
            double cb = ((Math.log((double) b + lso) - o) * r) + 1.0D;
            if (Math.round(cb) != lcb) {
                bands.add(new Band(b + sso));
                lcb = (int) Math.round(cb);
            }
        }

        if (bands.size() < bandCount) {
            bands.add(new Band((hss - 1) + sso));
        }

        return bands.toArray(new Band[bands.size()]);
    }


    public double[] createBinGainTable(int binCount, float sampleRate) {
        float[] fqt = calculateFrequencyTable(binCount, sampleRate);
        binGainTable = new double[binCount];
        for (int i = 0; i < binCount; i++) {
            binGainTable[i] = (((fqt[i] / linearBinGain) + 512.0f) / 512.0f) * (linearBinGain * 1.5f);
        }
        return binGainTable;
    }

    public static float[] calculateFrequencyTable(int spectrumLength, float sampleRate) {
        float maxFreq = sampleRate / 2.0f;
        float binWidth = maxFreq / spectrumLength;
        float[] freqTable = new float[spectrumLength];

        int bin = 0;
        for (float binFreq = binWidth; binFreq <= maxFreq; binFreq += binWidth) {
            freqTable[bin] = binFreq;
            bin++;
        }
        return freqTable;
    }

    public void calculateWindowCoefficients(int windowLength) {
        if (windowCoefficients == null || windowCoefficients.length != windowLength) {
            windowCoefficients = new float[windowLength];
            for (int k = 0; k < windowLength; k++) {
                windowCoefficients[k] = (float) (0.54 - 0.46 * Math.cos(2.0 * java.lang.Math.PI * k / windowLength));
            }
        }
    }

    public void applyWindow(int fftWindowLength, double[] audioChannels) {
        double tempDouble;
        for (int i = 0; i < fftWindowLength; i++) {
            tempDouble = audioChannels[i] * windowCoefficients[i];
            audioChannels[i] = tempDouble;
        }
    }

    public void extractData() {
        long lfp = sourceDataLine.getLongFramePosition();  // long frame position
        int channelNum;
        int sampleNum;
        int cdp;
        int position;
        int bit;
        int bytePos;
        float signMask;

        synchronized (audioDataBuffer) {
            int offset = (int) ((lfp * frameSize) % (long) (audioDataBuffer.capacity()));

            for (sampleNum = 0, position = offset; sampleNum < blockLength; sampleNum++, position += frameSize) {
                if (position >= audioDataBuffer.capacity()) {
                    position = 0;
                }

                for (channelNum = 0, cdp = 0; channelNum < channelCount; channelNum++, cdp += channelSize) {

                    signMask = (audioDataBuffer.get(position + cdp) & 0xFF) - 128.0F;

                    for (bit = 8, bytePos = 1; bit < sampleSizeInBits; bit += 8) {
                        signMask += audioDataBuffer.get(position + cdp + bytePos) << bit;
                        bytePos++;
                    }

                    audioChannels[channelNum][sampleNum] = signMask / audioSampleSize;
                }
            }
        }
        if(channelCount == 1) {
            for (sampleNum = 0; sampleNum < blockLength; sampleNum++) {
                audioChannels[1][sampleNum] = audioChannels[0][sampleNum];
            }
        }
    }

    public double[] averageChannels(double[][] audioChannels) {
        int length = audioChannels[0].length;
        int channelCount = audioChannels.length;
        double[] outputSamples = new double[length];

        for (int sampleNum = 0; sampleNum < length; sampleNum++) {
            float sum = 0;
            for (double[] audioChannel : audioChannels) {
                sum += audioChannel[sampleNum];
            }
            outputSamples[sampleNum] = sum / (float) channelCount;
        }

        return outputSamples;
    }

    public void writeAudioData(byte[] audioData, int offset, int length) {
        synchronized (audioDataBuffer) {
            if (audioDataBuffer == null) {
                return;
            }

            if (audioDataBuffer.remaining() < length) {
                audioDataBuffer.clear();
            }
            audioDataBuffer.put(audioData, offset, length);
        }
    }

    public synchronized double[] getFreqTable() {
        return freqTable;
    }

    private void start() {
        (new Thread(this)).start();
    }

    private void stop() {
        isRunning = false;
    }

    private void close() {
        isRunning = false;
        if (audioDataBuffer != null) {
            audioDataBuffer.clear();
        }
    }

    public synchronized float getStrenghest() {
        return (float)strength;
    }

    public synchronized float getAverage() {
        return (float)average;
    }
}
