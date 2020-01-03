// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Scene.GUI;

import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.joml.Matrix4f;
import NeonFall.Rendering.Renderer;
import NeonFall.Manager.DisplayManager;
import NeonFall.Manager.InputManager;
import org.joml.Vector3f;
import org.joml.Vector4f;
import NeonFall.Manager.ResourceManager;
import java.util.ArrayList;
import NeonFall.Physics.AABB;
import NeonFall.Rendering.Camera;
import NeonFall.World.Entities.TexturedEntity;

public class Button extends TexturedEntity
{
    private String caption;
    private Camera camera;
    private AABB cube;
    private ArrayList<IButtonActionListener> listeners;
    
    public Button(final String caption, final Camera camera) {
        super(ResourceManager.CUBE_MODEL_FILE, ResourceManager.TEX_CUBE_FILE, new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
        this.cube = new AABB(new Vector3f(-1.0f, -1.0f, -1.0f), new Vector3f(1.0f, 1.0f, 1.0f));
        this.camera = camera;
        this.caption = caption;
        this.listeners = new ArrayList<IButtonActionListener>();
    }
    
    @Override
    public void update(final float delta) {
        if (this.isHover((float)InputManager.getMouseX(), (float)(DisplayManager.getHeight() - InputManager.getMouseY()))) {
            this.color.set(0.0f, 0.0f, 1.0f, 1.0f);
            if (InputManager.isMouseButtonPressed(0)) {
                this.click();
                this.color.set(1.0f, 0.0f, 0.0f, 1.0f);
            }
        }
        else {
            this.color.set(0.0f, 1.0f, 0.0f, 1.0f);
        }
    }
    
    @Override
    public void draw(final Renderer renderer) {
        super.draw(renderer);
    }
    
    private boolean isHover(final float x, final float y) {
        final Vector3f v0 = new Vector3f(this.camera.getPosition());
        final Vector3f v2 = new Vector3f();
        final Matrix4f inverse = new Matrix4f();
        this.modelMatrix.invert(inverse);
        final IntBuffer buffer = BufferUtils.createIntBuffer(4);
        buffer.put(0);
        buffer.put(0);
        buffer.put(DisplayManager.getWidth());
        buffer.put(DisplayManager.getHeight());
        buffer.flip();
        final Vector4f vec = new Vector4f();
        Matrix4f.unproject(x, y, 1.0f, this.camera.getProjectionMatrix(), this.camera.getViewMatrix(), buffer, new Matrix4f(), vec);
        v2.set(vec.x, vec.y, vec.z);
        inverse.transformPoint(v0);
        inverse.transformPoint(v2);
        return this.cube.intersectRay(v0, v2);
    }
    
    private void click() {
        this.listeners.forEach(IButtonActionListener::actionPerformed);
    }
    
    public void addListener(final IButtonActionListener listener) {
        this.listeners.add(listener);
    }
}
