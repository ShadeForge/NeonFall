package NeonFall.Rendering.Shader;

import NeonFall.Manager.ResourceManager;
import NeonFall.Resources.Model.Material;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Usage:
 * Author: lbald
 * Last Update: 30.12.2015
 */
public class StaticShaderProgram extends ShaderProgram {

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
        location_modelMatrix 		= super.getUniformLocation("modelMatrix");
        location_viewMatrix 			= super.getUniformLocation("viewMatrix");
        location_projectionMatrix 	= super.getUniformLocation("projectionMatrix");

        location_diffuse = super.getUniformLocation("diffuse");

        location_textureSampler = super.getUniformLocation("sampler");
    }

    public void loadMaterial(Material mat){
        super.loadVector(location_diffuse, 	mat.getDiffuse());
    }

    public void loadColor(Vector4f color) {
        super.loadVector(location_diffuse, color);
    }

    public void loadTextures() {
        super.loadInt(location_textureSampler, 0);
    }

    public void loadModelMatrix(Matrix4f matrix){
        super.loadMatrix(location_modelMatrix, matrix);
    }

    public void loadViewMatrix (Matrix4f view){
        super.loadMatrix(location_viewMatrix, view);
    }

    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(location_projectionMatrix, projection);
    }
}
