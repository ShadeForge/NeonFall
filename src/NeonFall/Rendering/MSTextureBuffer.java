// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Rendering;

import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class MSTextureBuffer extends TextureBuffer
{
    private int msBuffer;
    private int msDepthBuffer;
    private int samples;
    private int msTexture;
    
    public MSTextureBuffer(final int width, final int height, final int samples) {
        this.width = width;
        this.height = height;
        this.samples = samples;
        this.initTextures();
        this.initBuffers();
    }
    
    @Override
    public void start(final int attachment, final boolean hasDepthBuffer) {
        super.start(attachment, hasDepthBuffer);
        GL30.glBindFramebuffer(36160, this.msBuffer);
        GL30.glBindRenderbuffer(36161, this.depthBufferID);
        GL30.glFramebufferTexture2D(36160, attachment, 37120, this.msTexture, 0);
        GL11.glClear(16640);
    }
    
    @Override
    public int stop() {
        GL30.glBindFramebuffer(36008, this.msBuffer);
        GL30.glBindFramebuffer(36009, this.frameBufferID);
        GL30.glBlitFramebuffer(0, 0, this.width, this.height, 0, 0, this.width, this.height, 16640, 9728);
        GL30.glBindFramebuffer(36008, 0);
        GL30.glBindFramebuffer(36009, 0);
        return super.stop();
    }
    
    @Override
    protected void initBuffers() {
        super.initBuffers();
        this.msBuffer = GL30.glGenFramebuffers();
        this.msDepthBuffer = GL30.glGenRenderbuffers();
        GL30.glBindFramebuffer(36160, this.msBuffer);
        GL30.glBindRenderbuffer(36161, this.msDepthBuffer);
        GL30.glRenderbufferStorageMultisample(36161, this.samples, 35056, this.width, this.height);
        GL30.glFramebufferRenderbuffer(36160, 33306, 36161, this.msDepthBuffer);
    }
    
    @Override
    protected void initTextures() {
        super.initTextures();
        GL11.glBindTexture(37120, this.msTexture = GL11.glGenTextures());
        GL32.glTexImage2DMultisample(37120, this.samples, 6407, this.width, this.height, true);
        GL11.glBindTexture(37120, 0);
    }
    
    @Override
    protected void destroyBuffers() {
        super.destroyBuffers();
        GL30.glDeleteFramebuffers(this.msBuffer);
        GL30.glDeleteFramebuffers(this.msDepthBuffer);
    }
    
    @Override
    protected void destroyTextures() {
        super.destroyTextures();
        GL11.glDeleteTextures(this.msTexture);
    }
}
