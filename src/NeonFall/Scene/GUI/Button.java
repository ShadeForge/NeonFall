package NeonFall.Scene.GUI;

import NeonFall.Manager.DisplayManager;
import NeonFall.Manager.InputManager;
import NeonFall.Manager.ResourceManager;
import NeonFall.Physics.AABB;
import NeonFall.Rendering.Camera;
import NeonFall.Rendering.Renderer;
import NeonFall.World.Entities.TexturedEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;

import java.nio.IntBuffer;
import java.util.ArrayList;

/**
 * Usage:
 * Author: lbald
 * Last Update: 30.12.2015
 */
public class Button extends TexturedEntity {
    private String caption;
    private Camera camera;
    private AABB cube;
    private ArrayList<IButtonActionListener> listeners;

    public Button(String caption, Camera camera) {
        super(ResourceManager.CUBE_MODEL_FILE, ResourceManager.TEX_CUBE_FILE, new Vector4f(0, 1, 0, 1));
        cube = new AABB(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));
        this.camera = camera;
        this.caption = caption;
        listeners = new ArrayList<>();
    }

    @Override
    public void update(float delta) {
        if(isHover(InputManager.getMouseX(), DisplayManager.getHeight() - InputManager.getMouseY())) {
            color.set(0, 0, 1, 1);

            if(InputManager.isMouseButtonPressed(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                click();
                color.set(1, 0, 0, 1);
            }
        } else {
            color.set(0, 1, 0, 1);
        }
    }

    @Override
    public void draw(Renderer renderer) {
        super.draw(renderer);

        // TODO: Draw Caption
    }

    private boolean isHover(float x, float y) {

        Vector3f v0 = new Vector3f(camera.getPosition());
        Vector3f v1 = new Vector3f();

        Matrix4f inverse = new Matrix4f();

        modelMatrix.invert(inverse);
        IntBuffer buffer = BufferUtils.createIntBuffer(4);
        buffer.put(0);
        buffer.put(0);
        buffer.put(DisplayManager.getWidth());
        buffer.put(DisplayManager.getHeight());
        buffer.flip();

        Vector4f vec = new Vector4f();
        Matrix4f.unproject(x, y, 1, camera.getProjectionMatrix(), camera.getViewMatrix(), buffer, new Matrix4f(), vec);
        v1.set(vec.x, vec.y, vec.z);

        inverse.transformPoint(v0);
        inverse.transformPoint(v1);

        return cube.intersectRay(v0, v1);
    }

    private void click() {
        listeners.forEach(IButtonActionListener::actionPerformed);
    }

    public void addListener(IButtonActionListener listener) {
        listeners.add(listener);
    }
}
