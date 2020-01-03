// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Scene;

import NeonFall.MainGame;
import org.joml.Vector3f;
import NeonFall.Scene.GUI.Button;

public class MainMenuScene extends Scene
{
    private Button startButton;
    private Button highscoreButton;
    private Button creditsButton;
    private Button quitButton;
    
    public MainMenuScene() {
        super(new Vector3f(0.0f, 8.0f, -10.0f), new Vector3f(0.3926991f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
        this.startButton = new Button("Start", this.camera);
        this.startButton.getModelMatrix().translate(0.0f, 6.0f, 0.0f).scale(2.0f, 1.0f, 1.0f);
        this.startButton.addListener(() -> MainGame.setScene(SCENE_TYPE.GAME));
        this.highscoreButton = new Button("Highscore", this.camera);
        this.highscoreButton.getModelMatrix().translate(3.0f, 3.0f, 0.0f).rotateXYZ(0.3926991f, 0.3926991f, 0.0f).scale(2.0f, 1.0f, 1.0f);
        this.highscoreButton.addListener(() -> MainGame.setScene(SCENE_TYPE.HIGHSCORE));
        this.creditsButton = new Button("Credits", this.camera);
        this.creditsButton.getModelMatrix().translate(-3.0f, 3.0f, 0.0f).rotateXYZ(0.3926991f, -0.3926991f, 0.0f).scale(2.0f, 1.0f, 1.0f);
        this.highscoreButton.addListener(() -> MainGame.setScene(SCENE_TYPE.CREDITS));
        this.quitButton = new Button("Quit", this.camera);
        this.quitButton.getModelMatrix().translate(0.0f, 0.0f, 0.0f).rotateXYZ(0.3926991f, 0.0f, 0.0f).scale(2.0f, 1.0f, 1.0f);
        this.quitButton.addListener(() -> System.exit(0));
    }
    
    @Override
    public void update(final float delta) {
        this.startButton.update(delta);
        this.highscoreButton.update(delta);
        this.creditsButton.update(delta);
        this.quitButton.update(delta);
    }
    
    @Override
    public void drawLights() {
        this.startButton.draw(this.renderer);
        this.highscoreButton.draw(this.renderer);
        this.creditsButton.draw(this.renderer);
        this.quitButton.draw(this.renderer);
    }
    
    @Override
    public void drawNoneLightEntities() {
    }
    
    @Override
    public void drawGUI() {
    }
    
    @Override
    public void resize(final int width, final int height) {
        super.resize(width, height);
        this.renderer.resize(width, height);
    }
    
    @Override
    public void destroy() {
        this.renderer.destroy();
    }
}
