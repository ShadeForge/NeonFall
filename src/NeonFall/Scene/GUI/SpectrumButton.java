package NeonFall.Scene.GUI;

import NeonFall.Rendering.Camera;
import NeonFall.Rendering.Renderer;
import NeonFall.Resources.Sounds.SpectrumSoundListener;
import org.joml.Vector3f;

import java.util.LinkedList;

/**
 * Usage:
 * Author: lbald
 * Last Update: 13.01.2016
 */
public class SpectrumButton extends Button {

    private static final int MAX_LAST_VALUES = 16;
    private static final float TRANS_SPEED = 20f;

    private SpectrumSoundListener listener;
    private LinkedList<Double> lastValues;

    public SpectrumButton(String caption, Camera camera, SpectrumSoundListener listener) {
        super(caption, camera);
        this.listener = listener;

        lastValues = new LinkedList<>();
        lastValues.add(0d);
    }

    public void update(float delta) {
        super.update(delta);

        updateLastValues();
        Vector3f diffuse = getMaterial().getDiffuse();
        float should;
        double testValue = lastValues.getFirst();
        double average = 0;
        for (Double lastValue : lastValues) {
            average += lastValue;
        }
        average /= MAX_LAST_VALUES;

        if(testValue > average)
            should = 1;
        else
            should = 0.25f;

        //should = testValue / 32f;
        float difference = should - diffuse.y;
        diffuse.y += ((difference * delta) * TRANS_SPEED);
        if(diffuse.y > 1.0f)
            diffuse.y = 1;
    }

    private void updateLastValues() {
        double[] values = listener.getFreqTable();
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
    public void draw(Renderer renderer) {
        super.draw(renderer);
    }

}
