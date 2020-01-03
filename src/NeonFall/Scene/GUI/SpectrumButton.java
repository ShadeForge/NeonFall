// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Scene.GUI;

import NeonFall.Rendering.Renderer;
import java.util.Iterator;
import org.joml.Vector3f;
import NeonFall.Rendering.Camera;
import java.util.LinkedList;
import NeonFall.Resources.Sounds.SpectrumSoundListener;

public class SpectrumButton extends Button
{
    private static final int MAX_LAST_VALUES = 16;
    private static final float TRANS_SPEED = 20.0f;
    private SpectrumSoundListener listener;
    private LinkedList<Double> lastValues;
    
    public SpectrumButton(final String caption, final Camera camera, final SpectrumSoundListener listener) {
        super(caption, camera);
        this.listener = listener;
        (this.lastValues = new LinkedList<Double>()).add(0.0);
    }
    
    @Override
    public void update(final float delta) {
        super.update(delta);
        this.updateLastValues();
        final Vector3f diffuse = this.getMaterial().getDiffuse();
        final double testValue = this.lastValues.getFirst();
        double average = 0.0;
        for (final Double lastValue : this.lastValues) {
            average += lastValue;
        }
        average /= 16.0;
        float should;
        if (testValue > average) {
            should = 1.0f;
        }
        else {
            should = 0.25f;
        }
        final float difference = should - diffuse.y;
        final Vector3f vector3f = diffuse;
        vector3f.y += difference * delta * 20.0f;
        if (diffuse.y > 1.0f) {
            diffuse.y = 1.0f;
        }
    }
    
    private void updateLastValues() {
        final double[] values = this.listener.getFreqTable();
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
    public void draw(final Renderer renderer) {
        super.draw(renderer);
    }
}
