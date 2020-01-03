package NeonFall.World.Entities;

import NeonFall.Manager.ResourceManager;
import NeonFall.Resources.Model.Material;
import NeonFall.Resources.Model.RawModel;
import NeonFall.Rendering.Renderer;
import org.joml.Matrix4f;

/**
 * Usage:
 * Author: lbald
 * Last Update: 30.12.2015
 */
public abstract class Entity {

    protected RawModel model;
    protected Material material;
    protected Matrix4f modelMatrix;

    public Entity(String file) {
        this.model = ResourceManager.getModel(file);
        this.modelMatrix = new Matrix4f();
        this.material = new Material(ResourceManager.TEST_MATERIAL);
    }

    public void draw(Renderer renderer) {
        renderer.renderEntity(this);
    }

    public abstract void update(float delta);

    public RawModel getModel() {
        return model;
    }
    public Material getMaterial() { return material; }
    public Matrix4f getModelMatrix() {
        return modelMatrix;
    }
}
