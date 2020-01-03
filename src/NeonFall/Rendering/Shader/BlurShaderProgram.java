package NeonFall.Rendering.Shader;

import NeonFall.Manager.ResourceManager;
import org.joml.Vector2f;

import static org.lwjgl.opengl.GL20.glUniform1i;

/**
 * Usage:
 * Author: lbald
 * Last Update: 09.01.2016
 */
public class BlurShaderProgram extends ShaderProgram {

    private int location_texelSize;
    private int location_sampler;

    private int location_orientation;
    private int location_blurAmount;
    private int location_blurScale;
    private int location_blurStrength;

    public BlurShaderProgram() {
        super(ResourceManager.IMAGE_VERTEX_FILE, ResourceManager.BLUR_FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    @Override
    protected void getAllUniformLocations() {
        location_texelSize = super.getUniformLocation("texelSize");
        location_sampler = super.getUniformLocation("sampler");

        location_orientation = super.getUniformLocation("orientation");
        location_blurAmount = super.getUniformLocation("blurAmount");
        location_blurScale = super.getUniformLocation("blurScale");
        location_blurStrength = super.getUniformLocation("blurStrength");
    }

    public void loadTexelSize(Vector2f size) {
        super.loadVector(location_texelSize, size);
    }

    public void loadTexture() {
        glUniform1i(location_sampler, 0);
    }

    public void loadOrientation(int orientation) {
        super.loadInt(location_orientation, orientation);
    }

    public void loadBlurAmount(int amount) {
        super.loadInt(location_blurAmount, amount);
    }

    public void loadBlurScale(float scale) {
        super.loadFloat(location_blurScale, scale);
    }

    public void loadBlurStrength(float strength) {
        super.loadFloat(location_blurStrength, strength);
    }
}
