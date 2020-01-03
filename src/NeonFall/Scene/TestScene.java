package NeonFall.Scene;

import NeonFall.Manager.InputManager;
import NeonFall.Manager.SoundManager;
import NeonFall.Rendering.Renderer;
import NeonFall.Resources.Sounds.SpectrumSoundListener;
import NeonFall.Scene.GUI.Button;
import NeonFall.Scene.GUI.SpectrumBarsBackground;
import NeonFall.Scene.GUI.SpectrumButton;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

/**
 * Usage:
 * Author: lbald
 * Last Update: 01.01.2016
 */
public class TestScene extends Scene {

    private SpectrumButton glowButton;
    private SpectrumButton spectrumButton;
    private Button blockButton;
    private SpectrumBarsBackground background;
    private SpectrumSoundListener spectrumSoundListener;

    public TestScene() {
        super(new Vector3f(0, 5, -20),
                new Vector3f((float)Math.PI/8, 0, 0),
                new Vector3f(0, 1, 0));

        spectrumSoundListener = new SpectrumSoundListener();
        SoundManager.musicPlayer.addListener(spectrumSoundListener);
        SoundManager.musicPlayer.play();

        background = new SpectrumBarsBackground(camera, spectrumSoundListener);

        blockButton = new Button("Just sitting here and block lights :3", camera);
        blockButton.getModelMatrix().translate(9.5f, 2, -1).scale(0.2f, 0.2f, 0.2f);
        blockButton.getMaterial().getDiffuse().set(0, 0, 1);
        blockButton.addListener(() -> System.out.println("Blocking liii~ghts"));

        glowButton = new SpectrumButton("I'M GLOWING!", camera, spectrumSoundListener);
        glowButton.getModelMatrix().translate(10, 2, 0).rotateXYZ(0, 0, 0);
        glowButton.addListener(() -> System.out.println("CLICKED ON GLOWING!"));

        spectrumButton = new SpectrumButton("None Glow", camera, spectrumSoundListener);
        spectrumButton.getModelMatrix().translate(-10, -2, 0).rotateXYZ(0, -45.0f, 0).scale(2, 1, 1);
        spectrumButton.addListener(() -> System.out.println("CLICKED ON ... not so cool glowing as the upper left cube ._."));
        renderer = new Renderer(camera);
    }

    @Override
    public void update(float delta) {

        if(InputManager.isKeyDown(GLFW.GLFW_KEY_LEFT))
            glowButton.getModelMatrix().rotateY(-0.05f);
        if(InputManager.isKeyDown(GLFW.GLFW_KEY_RIGHT))
            glowButton.getModelMatrix().rotateY(0.05f);
        if(InputManager.isKeyDown(GLFW.GLFW_KEY_UP))
            glowButton.getModelMatrix().rotateX(0.05f);
        if(InputManager.isKeyDown(GLFW.GLFW_KEY_DOWN))
            glowButton.getModelMatrix().rotateX(-0.05f);

        glowButton.update(delta);
        spectrumButton.update(delta);
        blockButton.update(delta);
        background.update(delta);
    }

    @Override
    public void drawLights() {
        glowButton.draw(renderer);
        background.draw(renderer);
    }

    @Override
    public void drawNoneLightEntities() {
        spectrumButton.draw(renderer);
        blockButton.draw(renderer);
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void destroy() {
        renderer.destroy();
    }
}
