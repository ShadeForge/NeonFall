// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.World;

import NeonFall.Scene.Scene;
import NeonFall.MainGame;
import java.nio.IntBuffer;
import NeonFall.Manager.DisplayManager;
import org.lwjgl.BufferUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.Iterator;
import NeonFall.Manager.SoundManager;
import org.joml.Vector4f;
import NeonFall.Manager.ResourceManager;
import NeonFall.World.Entities.TexturedEntity;
import NeonFall.Rendering.Renderer;
import NeonFall.Resources.Sounds.SpectrumSoundListener;
import NeonFall.Scene.GUI.SpectrumBarsBackground;
import NeonFall.Rendering.Camera;
import NeonFall.World.Entities.BarEntity;
import java.util.LinkedList;
import org.joml.Vector2f;
import NeonFall.World.Entities.Player;

public class RoundData
{
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
    
    public RoundData(final Camera camera, final Vector2f mapSize, final Renderer renderer) {
        this.renderer = renderer;
        this.camera = camera;
        this.mapSize = mapSize;
        this.barEntities = new LinkedList[(int)mapSize.x][(int)mapSize.y];
        this.removing = new LinkedList<BarEntity>();
        this.loosed = false;
        this.score = 0;
        this.fontPlane = new TexturedEntity("Plane_Model", ResourceManager.TEX_WHITE_FILE, new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
        for (int x = 0; x < mapSize.x; ++x) {
            for (int y = 0; y < mapSize.y; ++y) {
                this.barEntities[x][y] = new LinkedList<BarEntity>();
            }
        }
        this.listener = new SpectrumSoundListener();
        this.worldGenerator = new WorldGenerator(this, this.listener);
        this.player = new Player(this, new Vector2f((float)((int)mapSize.x / 2), (float)((int)mapSize.y / 2 + 1)));
        SoundManager.musicPlayer.addListener(this.listener);
        SoundManager.musicPlayer.play();
        this.background = new SpectrumBarsBackground(camera, this.listener);
    }
    
    public void update(final float delta) {
        this.player.update(delta);
        if (this.loosed) {
            this.startLosing();
            return;
        }
        this.score += (int)(delta * 1000.0f);
        this.background.update(delta);
        for (int x = 0; x < this.mapSize.x; ++x) {
            for (int y = 0; y < this.mapSize.y; ++y) {
                for (final BarEntity barEntity : this.barEntities[x][y]) {
                    barEntity.update(delta);
                }
            }
        }
        for (final BarEntity remove : this.removing) {
            final Vector3f pos = remove.getPosition();
            this.barEntities[(int)pos.x][(int)pos.y].remove(remove);
        }
        this.worldGenerator.update(delta);
    }
    
    public void drawLights(final Renderer renderer) {
        for (int x = 0; x < this.mapSize.x; ++x) {
            for (int y = 0; y < this.mapSize.y; ++y) {
                for (final BarEntity barEntity : this.barEntities[x][y]) {
                    barEntity.draw(renderer);
                }
            }
        }
        this.background.draw(renderer);
    }
    
    public void drawNoneLightEntities(final Renderer renderer) {
        this.player.draw(renderer);
        final String temp = String.valueOf(this.score);
        for (int i = 0; i < temp.length(); ++i) {
            final Matrix4f mat = new Matrix4f();
            final Vector4f vec = new Vector4f();
            final IntBuffer buffer = BufferUtils.createIntBuffer(4);
            buffer.put(0);
            buffer.put(0);
            buffer.put(DisplayManager.getWidth());
            buffer.put(DisplayManager.getHeight());
            buffer.flip();
            this.fontPlane.setTexture(ResourceManager.getTexture(ResourceManager.getNumberTexture(Integer.parseInt(temp.substring(i, i + 1)))));
            Matrix4f.unproject((float)(DisplayManager.getWidth() / 2), (float)(DisplayManager.getHeight() / 2), 0.0f, this.camera.getProjectionMatrix(), this.camera.getViewMatrix(), buffer, new Matrix4f(), vec);
            mat.translate(vec.x, vec.y, vec.z);
            mat.scale(0.1f);
            this.fontPlane.getModelMatrix().set(mat);
        }
    }
    
    public boolean isBlocked(final int x, final int y) {
        if (!this.isInMap(x, y)) {
            return false;
        }
        final LinkedList<BarEntity> barEntities = this.barEntities[x][y];
        if (barEntities.size() == 0) {
            return false;
        }
        final BarEntity bar = barEntities.getFirst();
        return bar.getPosition().z - bar.getLength() < 2.0f && bar.getPosition().z + bar.getLength() > 2.0f;
    }
    
    private void startLosing() {
        MainGame.highscore.add(this.score);
        MainGame.highscore.sort((o1, o2) -> o2.compareTo(o1));
        SoundManager.musicPlayer.stop();
        MainGame.setScene(Scene.SCENE_TYPE.MAINMENU);
    }
    
    public boolean isInMap(final int x, final int y) {
        return x >= 0 && y >= 0 && x < this.mapSize.x && y < this.mapSize.y;
    }
}
