// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Rendering.Shader;

import org.joml.Matrix4f;
import org.joml.Vector4f;
import NeonFall.Resources.Model.Material;
import NeonFall.Manager.ResourceManager;

public class StaticShaderProgram extends ShaderProgram
{
    private int location_modelMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_diffuse;
    private int location_textureSampler;
    
    public StaticShaderProgram() {
        super(ResourceManager.MAIN_VERTEX_FILE, ResourceManager.MAIN_FRAGMENT_FILE);
    }
    
    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "normal");
        super.bindAttribute(2, "textureCoordinates");
    }
    
    @Override
    protected void getAllUniformLocations() {
        this.location_modelMatrix = super.getUniformLocation("modelMatrix");
        this.location_viewMatrix = super.getUniformLocation("viewMatrix");
        this.location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        this.location_diffuse = super.getUniformLocation("diffuse");
        this.location_textureSampler = super.getUniformLocation("sampler");
    }
    
    public void loadMaterial(final Material mat) {
        super.loadVector(this.location_diffuse, mat.getDiffuse());
    }
    
    public void loadColor(final Vector4f color) {
        super.loadVector(this.location_diffuse, color);
    }
    
    public void loadTextures() {
        super.loadInt(this.location_textureSampler, 0);
    }
    
    public void loadModelMatrix(final Matrix4f matrix) {
        super.loadMatrix(this.location_modelMatrix, matrix);
    }
    
    public void loadViewMatrix(final Matrix4f view) {
        super.loadMatrix(this.location_viewMatrix, view);
    }
    
    public void loadProjectionMatrix(final Matrix4f projection) {
        super.loadMatrix(this.location_projectionMatrix, projection);
    }
}
