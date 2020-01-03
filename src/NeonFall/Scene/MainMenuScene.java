package NeonFall.Scene;

import NeonFall.MainGame;
import NeonFall.Scene.GUI.Button;
import org.joml.Vector3f;

/**
 * Usage:
 * Author: lbald
 * Last Update: 29.12.2015
 */
public class MainMenuScene extends Scene {

    private Button startButton;
    private Button highscoreButton;
    private Button creditsButton;
    private Button quitButton;

    public MainMenuScene() {
        super(new Vector3f(0, 8, -10),         //CamPos
                new Vector3f((float)Math.PI/8, 0, 0),         //CamDir
                new Vector3f(0, 1, 0));        //Up
        startButton = new Button("Start", camera);
        startButton.getModelMatrix().translate(0, 6, 0).scale(2, 1, 1);
        startButton.addListener(() -> MainGame.setScene(SCENE_TYPE.GAME));
        highscoreButton = new Button("Highscore", camera);
        highscoreButton.getModelMatrix().translate(3, 3, 0).rotateXYZ((float) Math.PI / 8, (float) Math.PI / 8, 0).scale(2, 1, 1);
        highscoreButton.addListener(() -> MainGame.setScene(SCENE_TYPE.HIGHSCORE));
        creditsButton = new Button("Credits", camera);
        creditsButton.getModelMatrix().translate(-3, 3, 0).rotateXYZ((float) Math.PI / 8, -(float) Math.PI / 8, 0).scale(2, 1, 1);
        highscoreButton.addListener(() -> MainGame.setScene(SCENE_TYPE.CREDITS));
        quitButton = new Button("Quit", camera);
        quitButton.getModelMatrix().translate(0, 0, 0).rotateXYZ((float) Math.PI / 8, 0, 0).scale(2, 1, 1);
        quitButton.addListener(() -> System.exit(0));
    }

    @Override
    public void update(float delta) {
        startButton.update(delta);
        highscoreButton.update(delta);
        creditsButton.update(delta);
        quitButton.update(delta);
    }

    @Override
    public void drawLights() {
        startButton.draw(renderer);
        highscoreButton.draw(renderer);
        creditsButton.draw(renderer);
        quitButton.draw(renderer);
    }

    @Override
    public void drawNoneLightEntities() {

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
