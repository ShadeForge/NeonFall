// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Rendering.Shader;

import org.lwjgl.opengl.GL20;
import NeonFall.Manager.ResourceManager;

public class GlowShaderProgram extends ShaderProgram
{
    private int location_glowMap_sampler;
    private int location_rendered_sampler;
    
    public GlowShaderProgram() {
        super(ResourceManager.IMAGE_VERTEX_FILE, ResourceManager.GLOW_FRAGMENT_FILE);
    }
    
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
    
    @Override
    protected void getAllUniformLocations() {
        this.location_rendered_sampler = super.getUniformLocation("renderedSampler");
        this.location_glowMap_sampler = super.getUniformLocation("glowSampler");
    }
    
    public void loadTexture() {
        GL20.glUniform1i(this.location_rendered_sampler, 0);
        GL20.glUniform1i(this.location_glowMap_sampler, 1);
    }
}
