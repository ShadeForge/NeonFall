// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Scene;

import org.joml.Vector3f;
import org.joml.Vector2f;
import NeonFall.Scene.GUI.FreeFallChargeBar;
import NeonFall.World.RoundData;

public class GameScene extends Scene
{
    private RoundData roundData;
    private FreeFallChargeBar chargeBar;
    private static final Vector2f mapSize = new Vector2f(11.0f);
    
    public GameScene() {
        super(new Vector3f(mapSize.x / 2.0f, mapSize.y / 2.0f + 2.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
        this.roundData = new RoundData(this.camera, mapSize, this.renderer);
        this.chargeBar = new FreeFallChargeBar(this.roundData.player);
    }
    
    @Override
    public void update(final float delta) {
        this.roundData.update(delta);
    }
    
    @Override
    public void drawLights() {
        this.roundData.drawLights(this.renderer);
    }
    
    @Override
    public void drawNoneLightEntities() {
        this.roundData.drawNoneLightEntities(this.renderer);
    }
    
    @Override
    public void drawGUI() {
        this.chargeBar.draw();
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
