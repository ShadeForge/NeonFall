// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Scene;

import org.joml.Vector3f;
import NeonFall.Rendering.Camera;
import NeonFall.Rendering.Renderer;

public abstract class Scene
{

    public enum SCENE_TYPE
    {
        MAINMENU,
        GAME,
        HIGHSCORE,
        CREDITS,
        INTRO
    }

    protected static Renderer renderer;
    protected Camera camera;
    
    protected Scene() {
    }
    
    public Scene(final Vector3f camPos, final Vector3f rotation, final Vector3f up) {
        this.camera = new Camera(camPos, rotation, up);

        if(renderer == null) {
            this.renderer = new Renderer(this.camera);
        } else {
            this.renderer.setCamera(this.camera);
        }
    }
    
    public void render() {
        this.renderer.renderScene(this);
    }
    
    public void draw() {
        this.drawLights();
        this.drawNoneLightEntities();
        this.drawGUI();
    }
    
    public void resize(final int width, final int height) {
        this.camera.resize();
    }
    
    public abstract void drawLights();
    
    public abstract void drawNoneLightEntities();
    
    public abstract void drawGUI();
    
    public abstract void update(final float p0);
    
    public abstract void destroy();
}
