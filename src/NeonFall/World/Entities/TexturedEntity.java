// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.World.Entities;

import NeonFall.Rendering.Renderer;
import NeonFall.Manager.ResourceManager;
import org.joml.Vector4f;
import NeonFall.Resources.Texture.ModelTexture;

public class TexturedEntity extends Entity
{
    protected ModelTexture texture;
    protected Vector4f color;
    
    public TexturedEntity(final String modelPath, final String texPath, final Vector4f color) {
        super(modelPath);
        this.color = color;
        this.texture = ResourceManager.getTexture(texPath);
    }
    
    @Override
    public void update(final float delta) {
    }
    
    @Override
    public void draw(final Renderer renderer) {
        renderer.renderTexturedEntity(this);
    }
    
    public void setTexture(final ModelTexture tex) {
        this.texture = tex;
    }
    
    public ModelTexture getTexture() {
        return this.texture;
    }
    
    public Vector4f getColor() {
        return this.color;
    }
}
