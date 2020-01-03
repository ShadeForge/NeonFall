package NeonFall.Scene;

import NeonFall.Rendering.Camera;
import NeonFall.Rendering.Renderer;
import org.joml.Vector3f;

/**
 * Usage:
 * Author: lbald
 * Last Update: 28.12.2015
 */
public abstract class Scene {

    public enum SCENE_TYPE {MAINMENU, GAME, HIGHSCORE, CREDITS, INTRO}

    protected Renderer renderer;
    protected Camera camera;

    protected Scene() {}

    public Scene(Vector3f camPos, Vector3f rotation, Vector3f up) {
        camera = new Camera(camPos, rotation, up);
        renderer = new Renderer(camera);
    }

    public void render() {
        renderer.renderScene(this);
    }

    public void draw() {
        drawLights();
        drawNoneLightEntities();
    }

    public abstract void drawLights();
    public abstract void drawNoneLightEntities();
    public abstract void update(float delta);
    public abstract void resize(int width, int height);
    public abstract void destroy();
}
