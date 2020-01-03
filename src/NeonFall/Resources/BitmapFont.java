package NeonFall.Resources;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class BitmapFont {

    private int fontTexture;

    public BitmapFont(String path) {
        // Create a new texture for the bitmap font.
        fontTexture = glGenTextures();
        // Bind the texture object to the GL_TEXTURE_2D target, specifying that it will be a 2D texture.
        glBindTexture(GL_TEXTURE_2D, fontTexture);
        // Use TWL's utility classes to load the png file.
        try {
            PNGDecoder decoder = new PNGDecoder(new FileInputStream(path));
            ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            // Load the previously loaded texture data into the texture object.
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE,
                    buffer);
            // Unbind the texture.
            glBindTexture(GL_TEXTURE_2D, 0);
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    /**
     * Renders text using a font bitmap.
     *
     * @param string the string to render
     * @param gridSize the dimensions of the bitmap grid (e.g. 16 -> 16x16 grid; 8 -> 8x8 grid)
     * @param x the x-coordinate of the bottom-left corner of where the string starts rendering
     * @param y the y-coordinate of the bottom-left corner of where the string starts rendering
     * @param characterWidth the width of the character
     * @param characterHeight the height of the character
     */
    public void renderString(String string, int gridSize, float x, float y,
                             float characterWidth, float characterHeight) {
        GL11.glPushAttrib(GL_TEXTURE_BIT | GL_ENABLE_BIT | GL_COLOR_BUFFER_BIT);
        glEnable(GL_CULL_FACE);
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, fontTexture);
        // Enable linear texture filtering for smoothed results.
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        // Enable additive blending. This means that the colours will be added to already existing colours in the
        // frame buffer. In practice, this makes the black parts of the texture become invisible.
        glEnable(GL_BLEND);
        glBlendFunc(GL_ONE, GL_ONE);
        // Store the current model-view matrix.
        GL11.glPushMatrix();
        // Offset all subsequent (at least up until 'glPopMatrix') vertex coordinates.
        GL11.glTranslatef(x, y, 0);
        GL11.glBegin(GL_QUADS);
        // Iterate over all the characters in the string.
        for (int i = 0; i < string.length(); i++) {
            // Get the ASCII-code of the character by type-casting to integer.
            int asciiCode = (int) string.charAt(i);
            // There are 16 cells in a texture, and a texture coordinate ranges from 0.0 to 1.0.
            final float cellSize = 1.0f / gridSize;
            // The cell's x-coordinate is the greatest integer smaller than remainder of the ASCII-code divided by the
            // amount of cells on the x-axis, times the cell size.
            float cellX = (asciiCode % gridSize) * cellSize;
            // The cell's y-coordinate is the greatest integer smaller than the ASCII-code divided by the amount of
            // cells on the y-axis.
            float cellY = (asciiCode / gridSize) * cellSize;
            GL11.glTexCoord2f(cellX, cellY + cellSize);
            GL11.glVertex2f(i * characterWidth / 3, y);
            GL11.glTexCoord2f(cellX + cellSize, cellY + cellSize);
            GL11.glVertex2f(i * characterWidth / 3 + characterWidth / 2, y);
            GL11.glTexCoord2f(cellX + cellSize, cellY);
            GL11.glVertex2f(i * characterWidth / 3 + characterWidth / 2, y + characterHeight);
            GL11.glTexCoord2f(cellX, cellY);
            GL11.glVertex2f(i * characterWidth / 3, y + characterHeight);
        }
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    public void cleanUp() {
        glDeleteTextures(fontTexture);
    }
}