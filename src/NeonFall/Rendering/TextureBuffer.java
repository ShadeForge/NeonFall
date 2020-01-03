// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Rendering;

import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL11;

public class TextureBuffer
{
    protected int outTexture;
    protected int frameBufferID;
    protected int depthBufferID;
    protected int width;
    protected int height;
    
    protected TextureBuffer() {
    }
    
    public TextureBuffer(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.initTextures();
        this.initBuffers();
    }
    
    public void start(final int attachment, final boolean hasDepthBuffer) {
        GL11.glViewport(0, 0, this.width, this.height);
        if (hasDepthBuffer) {
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
        }
        else {
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
        }
        GL30.glBindFramebuffer(36160, this.frameBufferID);
        GL30.glBindRenderbuffer(36161, this.depthBufferID);
        GL30.glFramebufferTexture2D(36160, attachment, 3553, this.outTexture, 0);
        GL11.glClear(16640);
    }
    
    public int stop() {
        GL30.glBindFramebuffer(36160, 0);
        GL30.glBindRenderbuffer(36161, 0);
        return this.outTexture;
    }
    
    public void activateTexture(final int gl_texture) {
        GL13.glActiveTexture(gl_texture);
        GL11.glBindTexture(3553, this.outTexture);
    }
    
    public void setOutTexture(final int outTexture) {
        this.outTexture = outTexture;
    }
    
    public int getOutTexture() {
        return this.outTexture;
    }
    
    public void resize(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.destroy();
        this.initTextures();
        this.initBuffers();
        GL30.glBindFramebuffer(36160, 0);
        GL30.glBindRenderbuffer(36161, 0);
    }
    
    public void destroy() {
        this.destroyBuffers();
        this.destroyTextures();
    }
    
    protected void initBuffers() {
        this.frameBufferID = GL30.glGenFramebuffers();
        this.depthBufferID = GL30.glGenRenderbuffers();
        GL30.glBindFramebuffer(36160, this.frameBufferID);
        GL30.glBindRenderbuffer(36161, this.depthBufferID);
        GL30.glRenderbufferStorage(36161, 33190, this.width, this.height);
        GL30.glFramebufferRenderbuffer(36160, 36096, 36161, this.depthBufferID);
    }
    
    protected void initTextures() {
        GL11.glBindTexture(3553, this.outTexture = GL11.glGenTextures());
        GL11.glTexParameterf(3553, 10241, 9729.0f);
        GL11.glTexImage2D(3553, 0, 32856, this.width, this.height, 0, 6408, 5124, 0L);
        GL11.glBindTexture(3553, 0);
    }
    
    protected void destroyBuffers() {
        GL30.glBindFramebuffer(36160, 0);
        GL30.glBindRenderbuffer(36161, 0);
        GL30.glDeleteFramebuffers(this.frameBufferID);
        GL30.glDeleteRenderbuffers(this.depthBufferID);
    }
    
    protected void destroyTextures() {
        GL11.glDeleteTextures(this.outTexture);
    }
}
