package NeonFall.Rendering;

import NeonFall.Manager.DisplayManager;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector3f;


public class Camera {

    public static final float FOV = 70;
    public static final float NEAR = 0.5f;
    public static final float FAR = 20000.0f;

    private Matrix4f viewMatrix = new Matrix4f();
    private Matrix4f projectionMatrix = new Matrix4f();
    private Vector3f camPos = new Vector3f();
    private Vector3f lookDir = new Vector3f();
    private Vector3f lookUpRotation = new Vector3f();
    private Vector3f up = new Vector3f();
    private Vector3f rotation = new Vector3f();
  
    /**
     * Constructor
     */
    public Camera(Vector3f camPos, Vector3f rotation, Vector3f up){
        this.projectionMatrix = createProjectionMatrix(NEAR, FAR);
        
        // set initial position //
        this.camPos = camPos;
        this.up = up;
        this.rotation = rotation;
        this.lookUpRotation.y = 1;

        updateViewMatrix();
    }

    /**
     * 
     * @return Returns the projection matrix that defines the field of view and near and far plane
     */
    public Matrix4f getProjectionMatrix(){
        return new Matrix4f(this.projectionMatrix);
    }

    /**
     * 
     * @return Returns the view matrix that defines the position and orientation of the camera
     */
    public Matrix4f getViewMatrix(){
        return new Matrix4f(this.viewMatrix);
    }
    
    /**
     * Moves the camera in the scene
     * @param move - Translation vector to be added to the current position
     */
    public void moveCamera(Vector3f move)
    {
        this.camPos = this.camPos.add(move);
        this.updateViewMatrix();
    }

    public void setCamPos(Vector3f pos) {
        this.setCamPos(pos.x, pos.y, pos.z);
    }

    public void setCamPos(float x, float y, float z) {
        this.camPos.x = x;
        this.camPos.y = y;
        this.camPos.z = z;
        this.updateViewMatrix();
    }

    /**
     * Update the view matrix based on the current position and orientation
     */
    public void updateViewMatrix(){
        // Compute new camera position //
        lookDir.x=0;
        lookDir.y=0;
        lookDir.z=1;

        lookDir = rotateToCamDir(lookDir);

        viewMatrix = new Matrix4f().lookAt(camPos, new Vector3f(lookDir).add(camPos), up);
    }

    public Vector3f rotateToCamDir(Vector3f vec) {
        Matrix3f mat = new Matrix3f();
        mat.rotateXYZ(rotation.x, rotation.y, rotation.z);
        mat.transform(vec);
        return vec;
    }

    public void setRotation(Vector3f rot) {
        rotation.set(rot);
        updateViewMatrix();
    }

    public void setRotation(float x, float y, float z) {
        rotation.set(x, y, z);
        updateViewMatrix();
    }

    public void setLookUpRotationXZ(float rotation) {
        up.x = -(float)Math.sin(rotation);
        up.y = (float)Math.cos(rotation);
        up.z = 0;

        updateViewMatrix();
    }

    public void setLookUp(Vector3f up) {
        this.up = up;
        updateViewMatrix();
    }

    /**
     * Creates the projection matrix of the camera
     * @param near - Near-plane
     * @param far - Far plane
     * @return Return the projection matrix that defines the field of view and near and far plane
     */
    private Matrix4f createProjectionMatrix(float near, float far){
        Matrix4f m = new Matrix4f();
        m.setPerspective(FOV, DisplayManager.getAspectRatio(), near, far);
        return m;
    }

    public Vector3f getPosition() {
        return camPos;
    }
    public Vector3f getLookDir() {
        return lookDir;
    }
}

