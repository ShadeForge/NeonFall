// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Scene.GUI;

import java.util.function.Consumer;
import NeonFall.Rendering.Renderer;
import org.joml.Vector3f;
import NeonFall.Resources.Sounds.SpectrumSoundListener;
import NeonFall.Rendering.Camera;
import org.joml.Matrix4f;
import java.util.ArrayList;

public class SpectrumBarsBackground
{
    private static final float BACKGROUND_DISTANCE = 100.0f;
    private ArrayList<SpectrumBar> spectrumBars;
    private Matrix4f modelMatrix;
    private Camera camera;
    private SpectrumSoundListener listener;
    
    public SpectrumBarsBackground(final Camera camera, final SpectrumSoundListener listener) {
        this.camera = camera;
        this.listener = listener;
        this.spectrumBars = new ArrayList<SpectrumBar>();
        final Vector3f pos = new Vector3f(0.0f, 0.0f, 1.0f);
        camera.rotateToCamDir(pos);
        pos.mul(100.0f);
        this.modelMatrix = new Matrix4f().translate(pos);
        for (int i = 0; i < 32; ++i) {
            final SpectrumBar spectrumBar = new SpectrumBar();
            this.spectrumBars.add(spectrumBar);
            final Matrix4f mat = new Matrix4f(this.modelMatrix);
            final Vector3f scale = new Vector3f();
            spectrumBar.getModelMatrix().getScale(scale);
            scale.y = 0.0f;
            mat.scale(scale);
            mat.translate((float)(2 * (i - 16)), 0.0f, 0.0f);
            spectrumBar.getModelMatrix().set(mat);
        }
    }
    
    public void update(final float delta) {
        final Vector3f pos = new Vector3f(0.0f, 0.0f, 1.0f);
        this.camera.rotateToCamDir(pos);
        pos.mul(100.0f);
        this.modelMatrix = new Matrix4f().translate(pos);
        final double[] frequencies = this.listener.getFreqTable();
        if (frequencies != null) {
            for (int i = 1; i < 32; ++i) {
                final Matrix4f mat = new Matrix4f(this.modelMatrix);
                final Vector3f scale = new Vector3f();
                this.spectrumBars.get(i).getModelMatrix().getScale(scale);
                final float should = 10.0f * (float)frequencies[i];
                final float difference = should - scale.y;
                final Vector3f vector3f = scale;
                vector3f.y += difference * delta * 25.0f + 0.01f;
                mat.scale(scale);
                mat.translate((float)(2 * (i - this.spectrumBars.size() / 2)), 0.0f, 0.0f);
                this.spectrumBars.get(i).getModelMatrix().set(mat);
            }
        }
    }
    
    public void draw(final Renderer renderer) {
        this.spectrumBars.forEach(renderer::renderEntity);
    }
    
    public Matrix4f getModelMatrix() {
        return this.modelMatrix;
    }
}
