package NeonFall.Rendering;

import org.lwjgl.opengl.GL14;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE2;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;

/**
 * Usage:
 * Author: lbald
 * Last Update: 11.01.2016
 */
public class TextureBuffer {

    protected int outTexture;

    protected int frameBufferID;
    protected int depthBufferID;

    protected int width;
    protected int height;

    protected TextureBuffer() {}

    public TextureBuffer(int width, int height) {
        this.width = width;
        this.height = height;

        initTextures();
        initBuffers();
    }

    public void start(int attachment, boolean hasDepthBuffer) {
        glViewport(0, 0, width, height);
        if(hasDepthBuffer) {
            glEnable(GL_DEPTH_TEST);
            glDepthMask(true);
        } else {
            glDisable(GL_DEPTH_TEST);
            glDepthMask(false);
        }
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
        glBindRenderbuffer(GL_RENDERBUFFER, depthBufferID);
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D, outTexture, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public int stop() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        return outTexture;
    }

    public void activateTexture(int gl_texture) {
        glActiveTexture(gl_texture);
        glBindTexture(GL_TEXTURE_2D, outTexture);
    }

    public void setOutTexture(int outTexture) {
        this.outTexture = outTexture;
    }

    public int getOutTexture() {
        return outTexture;
    }

    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
        destroy();
        initTextures();
        initBuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
    }

    public void destroy() {
        destroyBuffers();
        destroyTextures();
    }

    protected void initBuffers() {
        frameBufferID = glGenFramebuffers();
        depthBufferID = glGenRenderbuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
        glBindRenderbuffer(GL_RENDERBUFFER, depthBufferID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL14.GL_DEPTH_COMPONENT24, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthBufferID);
    }

    protected void initTextures() {
        outTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, outTexture);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_INT, 0);
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    protected void destroyBuffers() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glDeleteFramebuffers(frameBufferID);
        glDeleteRenderbuffers(depthBufferID);
    }

    protected void destroyTextures() {
        glDeleteTextures(outTexture);
    }
}
