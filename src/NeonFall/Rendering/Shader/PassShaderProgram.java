// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Rendering.Shader;

import org.joml.Vector4f;
import NeonFall.Manager.ResourceManager;

public class PassShaderProgram extends ShaderProgram
{
    private int location_diffuse;
    private int location_textureSampler;
    
    public PassShaderProgram() {
        super(ResourceManager.TEXT_VERTEX_FILE, ResourceManager.TEXT_FRAGMENT_FILE);
        this.loadVector(this.location_diffuse, new Vector4f(1.0f));
    }
    
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "texCoordinates");
    }
    
    @Override
    protected void getAllUniformLocations() {
        this.location_diffuse = super.getUniformLocation("diffuse");
        this.location_textureSampler = super.getUniformLocation("sampler");
    }
}
