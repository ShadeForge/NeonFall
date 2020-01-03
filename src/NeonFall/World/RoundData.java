package NeonFall.World;

import NeonFall.MainGame;
import NeonFall.Manager.DisplayManager;
import NeonFall.Manager.ResourceManager;
import NeonFall.Manager.SoundManager;
import NeonFall.Rendering.Camera;
import NeonFall.Rendering.Renderer;
import NeonFall.Resources.Sounds.SpectrumSoundListener;
import NeonFall.Resources.Texture.ModelTexture;
import NeonFall.Scene.GUI.SpectrumBarsBackground;
import NeonFall.Scene.Scene;
import NeonFall.World.Entities.BarEntity;
import NeonFall.World.Entities.Player;
import NeonFall.World.Entities.TexturedEntity;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import java.nio.BufferUnderflowException;
import java.nio.IntBuffer;
import java.util.LinkedList;

/**
 * Usage:
 * Author: lbald
 * Last Update: 14.01.2016
 */
public class RoundData {

    public Player player;
    public Vector2f mapSize;
    public LinkedList<BarEntity>[][] barEntities;
    public WorldGenerator worldGenerator;
    public Camera camera;
    public LinkedList<BarEntity> removing;
    public boolean loosed;
    public int score;
    private SpectrumBarsBackground background;
    private SpectrumSoundListener listener;
    private Renderer renderer;
    private TexturedEntity fontPlane;

    public RoundData(Camera camera, Vector2f mapSize, Renderer renderer) {
        this.renderer = renderer;
        this.camera = camera;
        this.mapSize = mapSize;
        this.barEntities = new LinkedList[(int) mapSize.x][(int) mapSize.y];
        this.removing = new LinkedList<>();
        this.loosed = false;
        this.score = 0;
        this.fontPlane = new TexturedEntity(ResourceManager.PLANE_MODEL_NAME, ResourceManager.TEX_WHITE_FILE, new Vector4f(0, 1, 0, 1));
        for(int x = 0; x < mapSize.x; x++) {
            for(int y = 0; y < mapSize.y; y++) {
                barEntities[x][y] = new LinkedList<>();
            }
        }
        listener = new SpectrumSoundListener();
        worldGenerator = new WorldGenerator(this, listener);
        player = new Player(this, new Vector2f((int)mapSize.x / 2, (int)mapSize.y / 2 + 1));
        SoundManager.musicPlayer.addListener(listener);
        SoundManager.musicPlayer.play();
        background = new SpectrumBarsBackground(camera, listener);
    }

    public void update(float delta) {
        player.update(delta);

        if(loosed) {
            startLosing();
            return;
        } else {
            score += delta * 1000;
        }

        background.update(delta);
        for(int x = 0; x < mapSize.x; x++) {
            for(int y = 0; y < mapSize.y; y++) {
                for (BarEntity barEntity : barEntities[x][y]) {
                    barEntity.update(delta);
                }
            }
        }
        for (BarEntity remove : removing) {
            Vector3f pos = remove.getPosition();
            barEntities[(int)pos.x][(int)pos.y].remove(remove);
        }
        worldGenerator.update(delta);
    }

    public void drawLights(Renderer renderer) {
        for(int x = 0; x < mapSize.x; x++) {
            for(int y = 0; y < mapSize.y; y++) {
                for (BarEntity barEntity : barEntities[x][y]) {
                    barEntity.draw(renderer);
                }
            }
        }
        background.draw(renderer);
    }

    public void drawNoneLightEntities(Renderer renderer) {
        player.draw(renderer);
        String temp = String.valueOf(score);

        for(int i = 0; i < temp.length(); i++) {
            Matrix4f mat = new Matrix4f();
            Vector4f vec = new Vector4f();

            IntBuffer buffer = BufferUtils.createIntBuffer(4);
            buffer.put(0);
            buffer.put(0);
            buffer.put(DisplayManager.getWidth());
            buffer.put(DisplayManager.getHeight());
            buffer.flip();

            fontPlane.setTexture(ResourceManager.getTexture(ResourceManager.getNumberTexture(Integer.parseInt(temp.substring(i, i + 1)))));
            Matrix4f.unproject(DisplayManager.getWidth() / 2, DisplayManager.getHeight() / 2, 0, camera.getProjectionMatrix(),
                    camera.getViewMatrix(), buffer, new Matrix4f(), vec);
            mat.translate(vec.x, vec.y, vec.z);
            mat.scale(0.1f);
            fontPlane.getModelMatrix().set(mat);
            //fontPlane.draw(renderer);
        }
    }

    public boolean isBlocked(int x, int y) {

        if(!isInMap(x, y))
            return false;
        LinkedList<BarEntity> barEntities = this.barEntities[x][y];

        if(barEntities.size() == 0)
            return false;

        BarEntity bar = barEntities.getFirst();

        return bar.getPosition().z - bar.getLength() < Player.PLAYER_POSITION_Z && bar.getPosition().z + bar.getLength() > Player.PLAYER_POSITION_Z;
    }

    private void startLosing() {
        MainGame.highscore.add(score);
        MainGame.highscore.sort((o1, o2) -> o2.compareTo(o1));
        SoundManager.musicPlayer.stop();
        MainGame.setScene(Scene.SCENE_TYPE.HIGHSCORE);
    }

    public boolean isInMap(int x, int y) {
        return x >= 0 && y >= 0 && x < mapSize.x && y < mapSize.y;
    }
}
