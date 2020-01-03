package NeonFall.World.Entities;

import NeonFall.Manager.ResourceManager;
import NeonFall.Rendering.Renderer;
import NeonFall.Resources.Texture.ModelTexture;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class TexturedEntity extends Entity {
    protected ModelTexture texture;
    protected Vector4f color;

    public TexturedEntity(String modelPath, String texPath, Vector4f color) {
        super(modelPath);
        this.color = color;
        this.texture = ResourceManager.getTexture(texPath);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(Renderer renderer) {
        renderer.renderTexturedEntity(this);
    }

    public void setTexture(ModelTexture tex) {
        this.texture = tex;
    }

    public ModelTexture getTexture() {
        return texture;
    }
    public Vector4f getColor() { return color; }
}
