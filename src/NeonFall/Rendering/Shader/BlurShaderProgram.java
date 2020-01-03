// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Rendering.Shader;

import org.lwjgl.opengl.GL20;
import org.joml.Vector2f;
import NeonFall.Manager.ResourceManager;

public class BlurShaderProgram extends ShaderProgram
{
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
        this.location_texelSize = super.getUniformLocation("texelSize");
        this.location_sampler = super.getUniformLocation("sampler");
        this.location_orientation = super.getUniformLocation("orientation");
        this.location_blurAmount = super.getUniformLocation("blurAmount");
        this.location_blurScale = super.getUniformLocation("blurScale");
        this.location_blurStrength = super.getUniformLocation("blurStrength");
    }
    
    public void loadTexelSize(final Vector2f size) {
        super.loadVector(this.location_texelSize, size);
    }
    
    public void loadTexture() {
        GL20.glUniform1i(this.location_sampler, 0);
    }
    
    public void loadOrientation(final int orientation) {
        super.loadInt(this.location_orientation, orientation);
    }
    
    public void loadBlurAmount(final int amount) {
        super.loadInt(this.location_blurAmount, amount);
    }
    
    public void loadBlurScale(final float scale) {
        super.loadFloat(this.location_blurScale, scale);
    }
    
    public void loadBlurStrength(final float strength) {
        super.loadFloat(this.location_blurStrength, strength);
    }
}
