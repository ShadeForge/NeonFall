// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Scene;

import NeonFall.Manager.InputManager;
import NeonFall.Rendering.Renderer;
import NeonFall.Manager.SoundManager;
import org.joml.Vector3f;
import NeonFall.Resources.Sounds.SpectrumSoundListener;
import NeonFall.Scene.GUI.SpectrumBarsBackground;
import NeonFall.Scene.GUI.Button;
import NeonFall.Scene.GUI.SpectrumButton;

public class TestScene extends Scene
{
    private SpectrumButton glowButton;
    private SpectrumButton spectrumButton;
    private Button blockButton;
    private SpectrumBarsBackground background;
    private SpectrumSoundListener spectrumSoundListener;
    
    public TestScene() {
        super(new Vector3f(0.0f, 5.0f, -20.0f), new Vector3f(0.3926991f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
        this.spectrumSoundListener = new SpectrumSoundListener();
        SoundManager.musicPlayer.addListener(this.spectrumSoundListener);
        SoundManager.musicPlayer.play();
        this.background = new SpectrumBarsBackground(this.camera, this.spectrumSoundListener);
        this.blockButton = new Button("Just sitting here and block lights :3", this.camera);
        this.blockButton.getModelMatrix().translate(9.5f, 2.0f, -1.0f).scale(0.2f, 0.2f, 0.2f);
        this.blockButton.getMaterial().getDiffuse().set(0.0f, 0.0f, 1.0f);
        this.blockButton.addListener(() -> System.out.println("Blocking liii~ghts"));
        this.glowButton = new SpectrumButton("I'M GLOWING!", this.camera, this.spectrumSoundListener);
        this.glowButton.getModelMatrix().translate(10.0f, 2.0f, 0.0f).rotateXYZ(0.0f, 0.0f, 0.0f);
        this.glowButton.addListener(() -> System.out.println("CLICKED ON GLOWING!"));
        this.spectrumButton = new SpectrumButton("None Glow", this.camera, this.spectrumSoundListener);
        this.spectrumButton.getModelMatrix().translate(-10.0f, -2.0f, 0.0f).rotateXYZ(0.0f, -45.0f, 0.0f).scale(2.0f, 1.0f, 1.0f);
        this.spectrumButton.addListener(() -> System.out.println("CLICKED ON ... not so cool glowing as the upper left cube ._."));
        this.renderer = new Renderer(this.camera);
    }
    
    @Override
    public void update(final float delta) {
        if (InputManager.isKeyDown(263)) {
            this.glowButton.getModelMatrix().rotateY(-0.05f);
        }
        if (InputManager.isKeyDown(262)) {
            this.glowButton.getModelMatrix().rotateY(0.05f);
        }
        if (InputManager.isKeyDown(265)) {
            this.glowButton.getModelMatrix().rotateX(0.05f);
        }
        if (InputManager.isKeyDown(264)) {
            this.glowButton.getModelMatrix().rotateX(-0.05f);
        }
        this.glowButton.update(delta);
        this.spectrumButton.update(delta);
        this.blockButton.update(delta);
        this.background.update(delta);
    }
    
    @Override
    public void drawLights() {
        this.glowButton.draw(this.renderer);
        this.background.draw(this.renderer);
    }
    
    @Override
    public void drawNoneLightEntities() {
        this.spectrumButton.draw(this.renderer);
        this.blockButton.draw(this.renderer);
    }
    
    @Override
    public void drawGUI() {
    }
    
    @Override
    public void resize(final int width, final int height) {
        this.renderer.resize(width, height);
    }
    
    @Override
    public void destroy() {
        this.renderer.destroy();
    }
}
