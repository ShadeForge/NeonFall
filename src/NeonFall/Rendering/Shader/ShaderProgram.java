// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Rendering.Shader;

import org.lwjgl.BufferUtils;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.joml.Vector4f;
import org.joml.Vector3f;
import org.joml.Vector2f;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL20;
import java.nio.FloatBuffer;

public abstract class ShaderProgram
{
    private int programID;
    private int vertexShaderID;
    private int fragmentShaderID;
    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
    
    public ShaderProgram(final String vertexFile, final String fragmentFile) {
        this.vertexShaderID = loadShader(vertexFile, 35633);
        this.fragmentShaderID = loadShader(fragmentFile, 35632);
        GL20.glAttachShader(this.programID = GL20.glCreateProgram(), this.vertexShaderID);
        GL20.glAttachShader(this.programID, this.fragmentShaderID);
        this.bindAttributes();
        GL20.glLinkProgram(this.programID);
        GL20.glValidateProgram(this.programID);
        this.getAllUniformLocations();
    }
    
    protected abstract void getAllUniformLocations();
    
    protected int getUniformLocation(final String uniformName) {
        return GL20.glGetUniformLocation(this.programID, uniformName);
    }
    
    public void start() {
        GL20.glUseProgram(this.programID);
    }
    
    public void stop() {
        GL20.glUseProgram(0);
    }
    
    public void cleanUp() {
        this.stop();
        GL20.glDetachShader(this.programID, this.vertexShaderID);
        GL20.glDetachShader(this.programID, this.fragmentShaderID);
        GL20.glDeleteShader(this.vertexShaderID);
        GL20.glDeleteShader(this.fragmentShaderID);
        GL20.glDeleteProgram(this.programID);
    }
    
    protected void loadMatrix(final int location, final Matrix4f matrix) {
        matrix.get(ShaderProgram.matrixBuffer);
        GL20.glUniformMatrix4fv(location, false, ShaderProgram.matrixBuffer);
    }
    
    protected void loadVector(final int location, final Vector2f vec) {
        GL20.glUniform2f(location, vec.x, vec.y);
    }
    
    protected void loadVector(final int location, final Vector3f vec) {
        GL20.glUniform3f(location, vec.x, vec.y, vec.z);
    }
    
    protected void loadVector(final int location, final Vector4f vec) {
        GL20.glUniform4f(location, vec.x, vec.y, vec.z, vec.w);
    }
    
    protected void loadInt(final int location, final int i) {
        GL20.glUniform1i(location, i);
    }
    
    protected void loadFloat(final int location, final float f) {
        GL20.glUniform1f(location, f);
    }
    
    protected abstract void bindAttributes();
    
    protected void bindAttribute(final int attribute, final String variableName) {
        GL20.glBindAttribLocation(this.programID, attribute, variableName);
    }
    
    protected static int loadShader(final String file, final int type) {
        final StringBuilder shaderSource = new StringBuilder();
        try {
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        final int shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        if (GL20.glGetShaderi(shaderID, 35713) == 0) {
            System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
            System.err.println("Could not compile shader!");
            System.exit(-1);
        }
        return shaderID;
    }
}
