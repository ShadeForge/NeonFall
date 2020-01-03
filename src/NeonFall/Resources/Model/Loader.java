// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Resources.Model;

import org.lwjgl.BufferUtils;
import java.nio.IntBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL15;
import java.util.function.Consumer;
import java.io.InputStream;
import java.io.IOException;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL11;
import java.nio.ByteBuffer;
import de.matthiasmann.twl.utils.PNGDecoder;
import java.io.FileInputStream;
import org.lwjgl.opengl.GL30;
import java.util.ArrayList;
import java.util.List;

public class Loader
{
    private List<Integer> vaos;
    private List<Integer> vbos;
    
    public Loader() {
        this.vaos = new ArrayList<Integer>();
        this.vbos = new ArrayList<Integer>();
    }
    
    public RawModel loadToVAO(final float[] positions, final int[] indices, final int dimensions) {
        final int vaoID = this.createVAO();
        GL30.glBindVertexArray(vaoID);
        this.createIndexBuffer(indices);
        this.createVertexBuffer(0, dimensions, positions);
        return new RawModel(vaoID, indices.length);
    }
    
    public RawModel loadToVAO(final float[] positions, final int[] indices, final float[] uv) {
        final int vaoID = this.createVAO();
        this.createIndexBuffer(indices);
        this.createVertexBuffer(0, 3, positions);
        this.createVertexBuffer(1, 2, uv);
        this.unbindVAO();
        return new RawModel(vaoID, indices.length);
    }
    
    public RawModel loadToVAO(final float[] positions, final float[] normals, final int[] indices) {
        final int vaoID = this.createVAO();
        this.createIndexBuffer(indices);
        this.createVertexBuffer(0, 3, positions);
        this.createVertexBuffer(1, 3, normals);
        this.unbindVAO();
        return new RawModel(vaoID, indices.length);
    }
    
    public RawModel loadToVAO(final float[] positions, final float[] textureCoords, final float[] normals, final int[] indices) {
        final int vaoID = this.createVAO();
        this.createIndexBuffer(indices);
        this.createVertexBuffer(0, 3, positions);
        this.createVertexBuffer(1, 3, normals);
        this.createVertexBuffer(2, 2, textureCoords);
        this.unbindVAO();
        return new RawModel(vaoID, indices.length);
    }
    
    public int loadTexture(final String fileName) {
        int textureID = 0;
        try {
            final InputStream in = new FileInputStream(fileName);
            final PNGDecoder decoder = new PNGDecoder(in);
            final ByteBuffer buf = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buf.flip();
            textureID = GL11.glGenTextures();
            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, textureID);
            GL11.glTexImage2D(3553, 0, 6408, decoder.getWidth(), decoder.getHeight(), 0, 6408, 5121, buf);
            GL30.glGenerateMipmap(3553);
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
            GL11.glTexParameteri(3553, 10241, 9987);
            GL11.glTexParameteri(3553, 10240, 9729);
            in.close();
        }
        catch (IOException e) {
            e.fillInStackTrace();
        }
        return textureID;
    }
    
    public void cleanUp() {
        this.vaos.forEach(GL30::glDeleteVertexArrays);
        this.vbos.forEach(GL15::glDeleteBuffers);
    }
    
    private int createVAO() {
        final int vaoID = GL30.glGenVertexArrays();
        this.vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }
    
    private void createVertexBuffer(final int attributeNumber, final int coordinateSize, final float[] data) {
        final int vboID = GL15.glGenBuffers();
        this.vbos.add(vboID);
        GL15.glBindBuffer(34962, vboID);
        final FloatBuffer buffer = this.storeDataInFloatBuffer(data);
        GL15.glBufferData(34962, buffer, 35044);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, 5126, false, 0, 0L);
        GL15.glBindBuffer(34962, 0);
    }
    
    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }
    
    private void createIndexBuffer(final int[] indices) {
        final int vboID = GL15.glGenBuffers();
        this.vbos.add(vboID);
        GL15.glBindBuffer(34963, vboID);
        final IntBuffer buffer = this.storeDataInIntBuffer(indices);
        GL15.glBufferData(34963, buffer, 35044);
    }
    
    private IntBuffer storeDataInIntBuffer(final int[] data) {
        final IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
    
    private FloatBuffer storeDataInFloatBuffer(final float[] data) {
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }
    
    public Material loadMaterial(final String filename) {
        return null;
    }
    
    public RawModel loadModel(final String filename) {
        return OBJLoader.loadObjModel(filename, this);
    }
    
    public RawModel loadModelOld(final String filename) {
        return OBJLoader.loadObjModelOld(filename, this);
    }
    
    public RawModel loadPlane() {
        return StaticLoader.loadPlane(this);
    }
}
