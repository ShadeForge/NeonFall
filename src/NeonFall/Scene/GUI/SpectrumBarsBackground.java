package NeonFall.Scene.GUI;

import NeonFall.Rendering.Camera;
import NeonFall.Rendering.Renderer;
import NeonFall.Resources.Sounds.SpectrumSoundListener;
import NeonFall.Resources.Sounds.SpectrumSoundThread;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * Usage:
 * Author: lbald
 * Last Update: 30.12.2015
 */
public class SpectrumBarsBackground {

    private static final float BACKGROUND_DISTANCE = 100f;

    private ArrayList<SpectrumBar> spectrumBars;
    private Matrix4f modelMatrix;
    private Camera camera;
    private SpectrumSoundListener listener;

    public SpectrumBarsBackground(Camera camera, SpectrumSoundListener listener) {
        this.camera = camera;
        this.listener = listener;

        spectrumBars = new ArrayList<>();
        Vector3f pos = new Vector3f(0, 0, 1);
        camera.rotateToCamDir(pos);
        pos.mul(BACKGROUND_DISTANCE);
        modelMatrix = new Matrix4f().translate(pos);

        for(int i = 0; i < 32; i++) {
            SpectrumBar spectrumBar = new SpectrumBar();
            spectrumBars.add(spectrumBar);
            Matrix4f mat = new Matrix4f(modelMatrix);
            Vector3f scale = new Vector3f();
            spectrumBar.getModelMatrix().getScale(scale);
            scale.y = 0;
            mat.scale(scale);
            mat.translate(2 * (i - 16), 0, 0);
            spectrumBar.getModelMatrix().set(mat);
        }
    }

    public void update(float delta) {
        Vector3f pos = new Vector3f(0, 0, 1);
        camera.rotateToCamDir(pos);
        pos.mul(BACKGROUND_DISTANCE);
        modelMatrix = new Matrix4f().translate(pos);
        double[] frequencies = listener.getFreqTable();

        if(frequencies != null) {
            for (int i = 1; i < 32; i++) {
                Matrix4f mat = new Matrix4f(modelMatrix);
                Vector3f scale = new Vector3f();
                spectrumBars.get(i).getModelMatrix().getScale(scale);
                float should = 10 * (float) frequencies[i];
                float difference = should - scale.y;
                scale.y += ((difference * delta) * 25f) + 0.01f;
                mat.scale(scale);
                mat.translate(2 * (i - spectrumBars.size() / 2), 0, 0);
                spectrumBars.get(i).getModelMatrix().set(mat);
            }
        }
    }

    public void draw(Renderer renderer) {
        spectrumBars.forEach(renderer::renderEntity);
    }

    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }
}
