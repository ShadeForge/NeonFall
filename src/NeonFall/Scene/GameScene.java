package NeonFall.Scene;

import NeonFall.Rendering.Camera;
import NeonFall.Rendering.Renderer;
import NeonFall.World.RoundData;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Usage:
 * Author: lbald
 * Last Update: 30.12.2015
 */
public class GameScene extends Scene {

    private RoundData roundData;

    public GameScene() {
        Vector2f mapSize = new Vector2f(11);
        camera = new Camera(new Vector3f(mapSize.x / 2, mapSize.y / 2 + 2, 0), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0));
        renderer = new Renderer(camera);
        roundData = new RoundData(camera, mapSize, renderer);
    }

    @Override
    public void update(float delta) {
        roundData.update(delta);
    }

    @Override
    public void drawLights() {
        roundData.drawLights(renderer);
    }

    @Override
    public void drawNoneLightEntities() {
        roundData.drawNoneLightEntities(renderer);
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
