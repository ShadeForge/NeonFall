// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Resources;

import java.nio.ByteBuffer;
import java.io.IOException;
import org.lwjgl.BufferUtils;
import java.io.InputStream;
import de.matthiasmann.twl.utils.PNGDecoder;
import java.io.FileInputStream;
import org.lwjgl.opengl.GL11;

public class BitmapFont
{
    private int fontTexture;
    private int gridSize;
    
    public BitmapFont(final String path, final int gridSize) {
        this.fontTexture = GL11.glGenTextures();
        this.gridSize = gridSize;
        GL11.glBindTexture(3553, this.fontTexture);
        try {
            final PNGDecoder decoder = new PNGDecoder(new FileInputStream(path));
            final ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            GL11.glTexImage2D(3553, 0, 6407, decoder.getWidth(), decoder.getHeight(), 0, 6408, 5121, buffer);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glBindTexture(3553, 0);
        }
        catch (IOException err) {
            err.printStackTrace();
        }
    }
    
    public void renderString(final String string, final float x, final float y, final float characterWidth, final float characterHeight) {
        GL11.glBindTexture(3553, this.fontTexture);
        GL11.glBegin(7);
        for (int i = 0; i < string.length(); ++i) {
            final int asciiCode = string.charAt(i);
            final float cellSize = 1.0f / this.gridSize;
            final float cellX = asciiCode % this.gridSize * cellSize;
            final float cellY = asciiCode / this.gridSize * cellSize;
            GL11.glTexCoord2f(0.0f, 0.0f);
            GL11.glVertex2f(x + i * characterWidth / 3.0f, y);
            GL11.glTexCoord2f(1.0f, 0.0f);
            GL11.glVertex2f(x + i * characterWidth / 3.0f + characterWidth / 2.0f, y);
            GL11.glTexCoord2f(1.0f, 1.0f);
            GL11.glVertex2f(x + i * characterWidth / 3.0f + characterWidth / 2.0f, y + characterHeight);
            GL11.glTexCoord2f(0.0f, 1.0f);
            GL11.glVertex2f(x + i * characterWidth / 3.0f, y + characterHeight);
        }
        GL11.glEnd();
    }
    
    public void cleanUp() {
        GL11.glDeleteTextures(this.fontTexture);
    }
}
