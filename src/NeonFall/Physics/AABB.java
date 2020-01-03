package NeonFall.Physics;

import org.joml.Vector3f;

/**
 * Usage:
 * Author: lbald
 * Last Update: 01.01.2016
 */
public class AABB {

    private Vector3f min;
    private Vector3f max;
    private Vector3f position;

    public AABB(Vector3f min, Vector3f max) {
        this.min = min;
        this.max = max;
        this.position = new Vector3f();
    }

    public boolean intersectRay(Vector3f v0, Vector3f v1) {

        float low = 0;
        float high = 1;
        float lowX = (min.x - v0.x) / (v1.x - v0.x);
        float highX = (max.x - v0.x) / (v1.x - v0.x);
        float lowY = (min.y - v0.y) / (v1.y - v0.y);
        float highY = (max.y - v0.y) / (v1.y - v0.y);
        float lowZ = (min.z - v0.z) / (v1.z - v0.z);
        float highZ = (max.z - v0.z) / (v1.z - v0.z);

        if (lowX > highX) {
            float temp = lowX;
            lowX = highX;
            highX = temp;
        }

        if (lowY > highY) {
            float temp = lowY;
            lowY = highY;
            highY = temp;
        }

        if (lowZ > highZ) {
            float temp = lowZ;
            lowZ = highZ;
            highZ = temp;
        }

        if(highX < low)
            return false;

        if(lowX > high)
            return false;

        low = Math.max(lowX, low);
        high = Math.min(highX, high);

        if(low > high)
            return false;

        if(highY < low)
            return false;

        if(lowY > high)
            return false;

        low = Math.max(lowY, low);
        high = Math.min(highY, high);

        if(low > high)
            return false;

        if(highZ < low)
            return false;

        if(lowZ > high)
            return false;

        low = Math.max(lowZ, low);
        high = Math.min(highZ, high);

        return low <= high;
    }

    public boolean intersectAABB(AABB box) {

        //Check intersect X
        if(min.x + position.x > box.max.x + box.position.x || max.x + position.x < box.min.x + box.position.x)
            return false;

        //Check intersect Y
        if(min.y  + position.y > box.max.y + box.position.y || max.y + position.y < box.min.y + box.position.y)
            return false;

        //Check intersect Z
        if(min.z + position.z > box.max.z + box.position.z || max.z + position.z < box.min.z + box.position.z)
            return false;

        return true;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setMin(Vector3f min) {
        this.min = min;
    }

    public void setMax(Vector3f max) {
        this.max = max;
    }

    public Vector3f getMin() {
        return min;
    }

    public Vector3f getMax() {
        return max;
    }
}
