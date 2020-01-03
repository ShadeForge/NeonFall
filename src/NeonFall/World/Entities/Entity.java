// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.World.Entities;

import NeonFall.Rendering.Renderer;
import NeonFall.Manager.ResourceManager;
import org.joml.Matrix4f;
import NeonFall.Resources.Model.Material;
import NeonFall.Resources.Model.RawModel;

public abstract class Entity
{
    protected RawModel model;
    protected Material material;
    protected Matrix4f modelMatrix;
    
    public Entity(final String file) {
        this.model = ResourceManager.getModel(file);
        this.modelMatrix = new Matrix4f();
        this.material = new Material(ResourceManager.TEST_MATERIAL);
    }
    
    public void draw(final Renderer renderer) {
        renderer.renderEntity(this);
    }
    
    public abstract void update(final float p0);
    
    public RawModel getModel() {
        return this.model;
    }
    
    public Material getMaterial() {
        return this.material;
    }
    
    public Matrix4f getModelMatrix() {
        return this.modelMatrix;
    }
}
