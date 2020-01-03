// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Rendering;

import NeonFall.Manager.DisplayManager;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Matrix4f;

public class Camera
{
    public static final float FOV = 70.0f;
    public static final float NEAR = 0.5f;
    public static final float FAR = 20000.0f;
    private Matrix4f viewMatrix;
    private Matrix4f projectionMatrix;
    private Vector3f camPos;
    private Vector3f lookDir;
    private Vector3f lookUpRotation;
    private Vector3f up;
    private Vector3f rotation;
    
    public Camera(final Vector3f camPos, final Vector3f rotation, final Vector3f up) {
        this.viewMatrix = new Matrix4f();
        this.projectionMatrix = new Matrix4f();
        this.camPos = new Vector3f();
        this.lookDir = new Vector3f();
        this.lookUpRotation = new Vector3f();
        this.up = new Vector3f();
        this.rotation = new Vector3f();
        this.projectionMatrix = this.createProjectionMatrix(0.5f, 20000.0f);
        this.camPos = camPos;
        this.up = up;
        this.rotation = rotation;
        this.lookUpRotation.y = 1.0f;
        this.updateViewMatrix();
    }
    
    public Matrix4f getProjectionMatrix() {
        return new Matrix4f(this.projectionMatrix);
    }
    
    public Matrix4f getViewMatrix() {
        return new Matrix4f(this.viewMatrix);
    }
    
    public void moveCamera(final Vector3f move) {
        this.camPos = this.camPos.add(move);
        this.updateViewMatrix();
    }
    
    public void setCamPos(final Vector3f pos) {
        this.setCamPos(pos.x, pos.y, pos.z);
    }
    
    public void setCamPos(final float x, final float y, final float z) {
        this.camPos.x = x;
        this.camPos.y = y;
        this.camPos.z = z;
        this.updateViewMatrix();
    }
    
    public void updateViewMatrix() {
        this.lookDir.x = 0.0f;
        this.lookDir.y = 0.0f;
        this.lookDir.z = 1.0f;
        this.lookDir = this.rotateToCamDir(this.lookDir);
        this.viewMatrix = new Matrix4f().lookAt(this.camPos, new Vector3f(this.lookDir).add(this.camPos), this.up);
    }
    
    public Vector3f rotateToCamDir(final Vector3f vec) {
        final Matrix3f mat = new Matrix3f();
        mat.rotateXYZ(this.rotation.x, this.rotation.y, this.rotation.z);
        mat.transform(vec);
        return vec;
    }
    
    public void setRotation(final Vector3f rot) {
        this.rotation.set(rot);
        this.updateViewMatrix();
    }
    
    public void setRotation(final float x, final float y, final float z) {
        this.rotation.set(x, y, z);
        this.updateViewMatrix();
    }
    
    public void setLookUpRotationXZ(final float rotation) {
        this.up.x = -(float)Math.sin(rotation);
        this.up.y = (float)Math.cos(rotation);
        this.up.z = 0.0f;
        this.updateViewMatrix();
    }
    
    public void setLookUp(final Vector3f up) {
        this.up = up;
        this.updateViewMatrix();
    }
    
    private Matrix4f createProjectionMatrix(final float near, final float far) {
        final Matrix4f m = new Matrix4f();
        m.setPerspective(70.0f, DisplayManager.getAspectRatio(), near, far);
        return m;
    }
    
    public void resize() {
        this.projectionMatrix = this.createProjectionMatrix(0.5f, 20000.0f);
    }
    
    public Vector3f getPosition() {
        return this.camPos;
    }
    
    public Vector3f getLookDir() {
        return this.lookDir;
    }
}
