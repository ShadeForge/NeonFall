package NeonFall.Resources.Model;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Loader {

	private List<Integer> vaos = new ArrayList<>();
	private List<Integer> vbos = new ArrayList<>();

    public RawModel loadToVAO(float[] positions, int[] indices, int dimensions) {

        int vaoID = createVAO();
        glBindVertexArray(vaoID);
		createIndexBuffer(indices);
        createVertexBuffer(0, dimensions, positions);

        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(float[] positions, int[] indices, float[] uv) {

        int vaoID = createVAO();
        createIndexBuffer(indices);
        createVertexBuffer(0, 3, positions);
        createVertexBuffer(1, 2, uv);
        unbindVAO();

        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(float[] positions, float[] normals, int[] indices){
        // create VAO and assign data
        int vaoID = createVAO();
        createIndexBuffer(indices);
        createVertexBuffer(0, 3, positions);
        createVertexBuffer(1, 3, normals);
        unbindVAO();

        // save VAO in RawModel
        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(float[] positions,  float[] textureCoords, float[] normals, int[] indices){
        // create VAO and assign data
        int vaoID = createVAO();
        createIndexBuffer(indices);
        createVertexBuffer(0, 3, positions);
        createVertexBuffer(1, 3, normals);
        createVertexBuffer(2, 2, textureCoords);
        unbindVAO();

        // save VAO in RawModel
        return new RawModel(vaoID, indices.length);
    }

	public int loadTexture(String fileName) {

		int textureID = 0;
		InputStream in;
		try {
			in = new FileInputStream(fileName);

			PNGDecoder decoder = new PNGDecoder(in);
			ByteBuffer buf = ByteBuffer.allocateDirect(4*decoder.getWidth()*decoder.getHeight());
			decoder.decode(buf, decoder.getWidth()*4, PNGDecoder.Format.RGBA);
			buf.flip();

			textureID = glGenTextures();
			glActiveTexture(GL_TEXTURE0);
			glBindTexture(GL_TEXTURE_2D, textureID);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
			glGenerateMipmap(GL_TEXTURE_2D);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

			in.close();
		} catch (IOException e) {
			e.fillInStackTrace();
		}

		return textureID;
	}

    public void cleanUp(){
        vaos.forEach(org.lwjgl.opengl.GL30::glDeleteVertexArrays);
        vbos.forEach(org.lwjgl.opengl.GL15::glDeleteBuffers);
    }

	private int createVAO(){
		int vaoID = glGenVertexArrays();
		vaos.add(vaoID);
		glBindVertexArray(vaoID);
		return vaoID;
	}

    private void createVertexBuffer(int attributeNumber,int coordinateSize, float[] data){
        // generate and save new ID for the vertex buffer object
        int vboID = glGenBuffers();
        vbos.add(vboID);

        // activate buffer and upload data
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        // tell OpenGL how to interpret the data
        glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0);

        // unbind buffer
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

	private void unbindVAO(){
		glBindVertexArray(0);
	}

	private void createIndexBuffer(int[] indices){
		// generate and save new ID for the index buffer object
		int vboID = glGenBuffers();
		vbos.add(vboID);

		// activate buffer and upload data
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
	}

	private IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

    public Material loadMaterial(String filename) {
        // TODO: Load Material
        return null;
    }

	public RawModel loadModel(String filename) {
		return OBJLoader.loadObjModel(filename, this);
	}

	public RawModel loadModelOld(String filename) {
		return OBJLoader.loadObjModelOld(filename, this);
	}

    public RawModel loadPlane() { return StaticLoader.loadPlane(this); }
}
