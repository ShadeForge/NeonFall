package NeonFall.Rendering;

import NeonFall.World.Entities.Entity;
import NeonFall.World.Entities.TexturedEntity;
import NeonFall.Manager.DisplayManager;
import NeonFall.Manager.ResourceManager;
import NeonFall.Rendering.Shader.BlurShaderProgram;
import NeonFall.Rendering.Shader.GlowShaderProgram;
import NeonFall.Rendering.Shader.StaticShaderProgram;
import NeonFall.Resources.Model.RawModel;
import NeonFall.Scene.Scene;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Usage:
 * Author: lbald
 * Last Update: 30.12.2015
 */
public class Renderer {

    private final static int DEFAULT_MSAA_SAMPLES = 8;

    private StaticShaderProgram mainShader;
    private BlurShaderProgram blurShader;
    private GlowShaderProgram glowShader;
    private TextureBuffer mainBuffer;
    private TextureBuffer blurBuffer;
    private TextureBuffer glowBuffer;
    private TextureBuffer glowDepthBuffer;
    private Camera camera;
    private RawModel surface;
    private Vector3f glowColor;

    public Renderer(Camera camera) {
        glowColor = new Vector3f(0, 1, 0);
        mainShader = new StaticShaderProgram();
        blurShader = new BlurShaderProgram();
        glowShader = new GlowShaderProgram();
        this.surface = ResourceManager.getModel(ResourceManager.SURFACE_MODEL_NAME);
        this.camera = camera;
        // set OpenGL parameters for rendering
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        initBuffers(DisplayManager.getWidth(), DisplayManager.getHeight());
    }

    private void initBuffers(int width, int height) {
        mainBuffer = new MSTextureBuffer(width, height, DEFAULT_MSAA_SAMPLES);
        glowBuffer = new MSTextureBuffer(width, height, DEFAULT_MSAA_SAMPLES);
        blurBuffer = new TextureBuffer(width, height);
        glowDepthBuffer = new TextureBuffer(width, height);
    }

    public void renderScene(Scene scene) {

        //FrameBuffer-Initialisation
        mainBuffer.start(GL_COLOR_ATTACHMENT0, true);

        mainShader.start();
        mainShader.loadProjectionMatrix(camera.getProjectionMatrix());
        mainShader.loadViewMatrix(camera.getViewMatrix());

        scene.draw();

        mainBuffer.stop();

        glowBuffer.start(GL_COLOR_ATTACHMENT0, true);

        glColorMask(false, false, false, false);
        scene.drawNoneLightEntities();

        glColorMask(true, true, true, true);
        scene.drawLights();

        glowBuffer.stop();

        mainShader.stop();

        blurShader.start();

        blurShader.loadTexture();

        blurBuffer.start(GL_COLOR_ATTACHMENT0, false);

        glowBuffer.activateTexture(GL_TEXTURE0);

        for(int i = 0; i < 4; i++) {

            blurShader.loadOrientation(i%2);

            renderFullScreenTexture();

            blurBuffer.activateTexture(GL_TEXTURE0);
        }

        blurBuffer.stop();

        blurShader.stop();

        glowShader.start();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        mainBuffer.activateTexture(GL_TEXTURE0);
        blurBuffer.activateTexture(GL_TEXTURE1);

        glowShader.loadTexture();

        renderFullScreenTexture();

        glowShader.stop();
    }

    private void renderFullScreenTexture() {
        glBindVertexArray(surface.getVaoID());
        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, surface.getVertexCount(), GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

    public void renderEntity(Entity entity) {

        // bind VAO and activate VBOs //
        RawModel model = entity.getModel();
        glBindVertexArray(model.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        // just now we can upload the model matrix //
        //mainShader.loadMaterial(entity.getMaterial());
        mainShader.loadModelMatrix(entity.getModelMatrix());

        // render model //
        glDrawElements(GL_TRIANGLES, model.getVertexCount(), GL_UNSIGNED_INT, 0);

        // a good programmer should clean up //
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
    }

    public void renderTexturedEntity(TexturedEntity e){
        //mainShader.loadMaterial(e.getMaterial());
        mainShader.loadColor(e.getColor());

        // bind VAO and activate VBOs //
        RawModel model = e.getModel();
        glBindVertexArray(model.getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(2);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, e.getTexture().getTextureID());

        // load model and projection matrix into shader //
        mainShader.loadModelMatrix(e.getModelMatrix());
        mainShader.loadProjectionMatrix(camera.getProjectionMatrix());

        // render model //
        glDrawElements(GL_TRIANGLES, model.getVertexCount(), GL_UNSIGNED_INT, 0);

        // a good programmer should clean up //
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(2);
        glBindVertexArray(0);
    }

    public Vector3f getGlowColor() {
        return glowColor;
    }

    public void resize(int width, int height) {
        mainBuffer.resize(width, height);
        glowBuffer.resize(width, height);
        blurBuffer.resize(width, height);
        glowDepthBuffer.resize(width, height);
    }

    public void destroy() {
        destroyBuffers();
        mainShader.cleanUp();
        blurShader.cleanUp();
        glowShader.cleanUp();
    }

    public void destroyBuffers() {
        mainBuffer.destroy();
        blurBuffer.destroy();
        glowBuffer.destroy();
        glowDepthBuffer.destroy();
    }
}
