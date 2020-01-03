package NeonFall.Rendering.Shader;

import NeonFall.Manager.ResourceManager;

import static org.lwjgl.opengl.GL20.glUniform1i;

/**
 * Usage:
 * Author: lbald
 * Last Update: 09.01.2016
 */
public class GlowShaderProgram extends ShaderProgram {

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
        location_rendered_sampler = super.getUniformLocation("renderedSampler");
        location_glowMap_sampler = super.getUniformLocation("glowSampler");
    }

    public void loadTexture(){
        glUniform1i(location_rendered_sampler, 0);
        glUniform1i(location_glowMap_sampler, 1);
    }
}
