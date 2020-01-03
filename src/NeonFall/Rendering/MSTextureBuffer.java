package NeonFall.Rendering;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.glTexImage2DMultisample;

/**
 * Usage:
 * Author: lbald
 * Last Update: 12.01.2016
 */
public class MSTextureBuffer extends TextureBuffer {

    private int msBuffer;
    private int msDepthBuffer;
    private int samples;

    private int msTexture;

    public MSTextureBuffer(int width, int height, int samples) {
        this.width = width;
        this.height = height;
        this.samples = samples;

        initTextures();
        initBuffers();
    }

    @Override
    public void start(int attachment, boolean hasDepthBuffer) {
        super.start(attachment, hasDepthBuffer);
        glBindFramebuffer(GL_FRAMEBUFFER, msBuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, depthBufferID);
        glFramebufferTexture2D(GL_FRAMEBUFFER, attachment, GL_TEXTURE_2D_MULTISAMPLE, msTexture, 0);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public int stop() {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, msBuffer);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, frameBufferID);
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        return super.stop();
    }

    @Override
    protected void initBuffers() {
        super.initBuffers();
        msBuffer = glGenFramebuffers();
        msDepthBuffer = glGenRenderbuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, msBuffer);
        glBindRenderbuffer(GL_RENDERBUFFER, msDepthBuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH24_STENCIL8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_STENCIL_ATTACHMENT, GL_RENDERBUFFER, msDepthBuffer);
    }

    @Override
    protected void initTextures() {
        super.initTextures();
        msTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, msTexture);
        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, GL_RGB, width, height, true);
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
    }

    @Override
    protected void destroyBuffers() {
        super.destroyBuffers();
        glDeleteFramebuffers(msBuffer);
        glDeleteFramebuffers(msDepthBuffer);
    }

    @Override
    protected void destroyTextures() {
        super.destroyTextures();
        glDeleteTextures(msTexture);
    }
}
