// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Rendering;

import org.lwjgl.opengl.GL13;
import NeonFall.World.Entities.TexturedEntity;
import NeonFall.World.Entities.Entity;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import NeonFall.Scene.Scene;
import NeonFall.Manager.DisplayManager;
import org.lwjgl.opengl.GL11;
import NeonFall.Manager.ResourceManager;
import org.joml.Vector3f;
import NeonFall.Resources.Model.RawModel;
import NeonFall.Rendering.Shader.PassShaderProgram;
import NeonFall.Rendering.Shader.GlowShaderProgram;
import NeonFall.Rendering.Shader.BlurShaderProgram;
import NeonFall.Rendering.Shader.StaticShaderProgram;

public class Renderer
{
    private static final int DEFAULT_MSAA_SAMPLES = 4;
    private StaticShaderProgram mainShader;
    private BlurShaderProgram blurShader;
    private GlowShaderProgram glowShader;
    private PassShaderProgram passShader;
    private TextureBuffer mainBuffer;
    private TextureBuffer blurBuffer;
    private TextureBuffer glowBuffer;
    private TextureBuffer glowDepthBuffer;
    private TextureBuffer textBuffer;
    private Camera camera;
    private RawModel surface;
    private Vector3f glowColor;
    
    public Renderer(final Camera camera) {
        this.glowColor = new Vector3f(0.0f, 1.0f, 0.0f);
        this.mainShader = new StaticShaderProgram();
        this.blurShader = new BlurShaderProgram();
        this.glowShader = new GlowShaderProgram();
        this.passShader = new PassShaderProgram();
        this.surface = ResourceManager.getModel("Surface_Model");
        this.camera = camera;
        GL11.glEnable(2884);
        GL11.glCullFace(1029);
        this.initBuffers(DisplayManager.getWidth(), DisplayManager.getHeight());
    }
    
    private void initBuffers(final int width, final int height) {
        this.mainBuffer = new MSTextureBuffer(width, height, 4);
        this.glowBuffer = new MSTextureBuffer(width, height, 4);
        this.blurBuffer = new TextureBuffer(width, height);
        this.glowDepthBuffer = new TextureBuffer(width, height);
        this.textBuffer = new TextureBuffer(width, height);
    }
    
    public void renderScene(final Scene scene) {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        this.mainBuffer.start(36064, true);
        this.mainShader.start();
        this.mainShader.loadProjectionMatrix(this.camera.getProjectionMatrix());
        this.mainShader.loadViewMatrix(this.camera.getViewMatrix());
        scene.draw();
        this.mainBuffer.stop();
        this.glowBuffer.start(36064, true);
        GL11.glColorMask(false, false, false, false);
        scene.drawNoneLightEntities();
        GL11.glColorMask(true, true, true, true);
        scene.drawLights();
        this.glowBuffer.stop();
        this.mainShader.stop();
        this.blurShader.start();
        this.blurShader.loadTexture();
        this.blurBuffer.start(36064, false);
        this.glowBuffer.activateTexture(33984);
        for (int i = 0; i < 4; ++i) {
            this.blurShader.loadOrientation(i % 2);
            this.renderFullScreenTexture();
            this.blurBuffer.activateTexture(33984);
        }
        this.blurBuffer.stop();
        this.blurShader.stop();
        this.glowShader.start();
        GL30.glBindFramebuffer(36160, 0);
        GL30.glBindRenderbuffer(36161, 0);
        this.mainBuffer.activateTexture(33984);
        this.blurBuffer.activateTexture(33985);
        this.glowShader.loadTexture();
        this.renderFullScreenTexture();
        this.glowShader.stop();
        this.passShader.start();
        scene.drawGUI();
        this.passShader.stop();
    }
    
    private void renderFullScreenTexture() {
        GL30.glBindVertexArray(this.surface.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDrawElements(4, this.surface.getVertexCount(), 5125, 0L);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }
    
    public void renderEntity(final Entity entity) {
        final RawModel model = entity.getModel();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        this.mainShader.loadModelMatrix(entity.getModelMatrix());
        GL11.glDrawElements(4, model.getVertexCount(), 5125, 0L);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }
    
    public void renderTexturedEntity(final TexturedEntity e) {
        this.mainShader.loadColor(e.getColor());
        final RawModel model = e.getModel();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(33984);
        GL11.glBindTexture(3553, e.getTexture().getTextureID());
        this.mainShader.loadModelMatrix(e.getModelMatrix());
        GL11.glDrawElements(4, model.getVertexCount(), 5125, 0L);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
    }
    
    public Vector3f getGlowColor() {
        return this.glowColor;
    }
    
    public void resize(final int width, final int height) {
        this.mainBuffer.resize(width, height);
        this.glowBuffer.resize(width, height);
        this.blurBuffer.resize(width, height);
        this.glowDepthBuffer.resize(width, height);
    }
    
    public void destroy() {
        this.destroyBuffers();
        this.mainShader.cleanUp();
        this.blurShader.cleanUp();
        this.glowShader.cleanUp();
    }
    
    public void destroyBuffers() {
        this.mainBuffer.destroy();
        this.blurBuffer.destroy();
        this.glowBuffer.destroy();
        this.glowDepthBuffer.destroy();
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }
}
