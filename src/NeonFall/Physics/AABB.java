// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Physics;

import org.joml.Vector3f;

public class AABB
{
    private Vector3f min;
    private Vector3f max;
    private Vector3f position;
    
    public AABB(final Vector3f min, final Vector3f max) {
        this.min = min;
        this.max = max;
        this.position = new Vector3f();
    }
    
    public boolean intersectRay(final Vector3f v0, final Vector3f v1) {
        float low = 0.0f;
        float high = 1.0f;
        float lowX = (this.min.x - v0.x) / (v1.x - v0.x);
        float highX = (this.max.x - v0.x) / (v1.x - v0.x);
        float lowY = (this.min.y - v0.y) / (v1.y - v0.y);
        float highY = (this.max.y - v0.y) / (v1.y - v0.y);
        float lowZ = (this.min.z - v0.z) / (v1.z - v0.z);
        float highZ = (this.max.z - v0.z) / (v1.z - v0.z);
        if (lowX > highX) {
            final float temp = lowX;
            lowX = highX;
            highX = temp;
        }
        if (lowY > highY) {
            final float temp = lowY;
            lowY = highY;
            highY = temp;
        }
        if (lowZ > highZ) {
            final float temp = lowZ;
            lowZ = highZ;
            highZ = temp;
        }
        if (highX < low) {
            return false;
        }
        if (lowX > high) {
            return false;
        }
        low = Math.max(lowX, low);
        high = Math.min(highX, high);
        if (low > high) {
            return false;
        }
        if (highY < low) {
            return false;
        }
        if (lowY > high) {
            return false;
        }
        low = Math.max(lowY, low);
        high = Math.min(highY, high);
        if (low > high) {
            return false;
        }
        if (highZ < low) {
            return false;
        }
        if (lowZ > high) {
            return false;
        }
        low = Math.max(lowZ, low);
        high = Math.min(highZ, high);
        return low <= high;
    }
    
    public boolean intersectAABB(final AABB box) {
        return this.min.x + this.position.x <= box.max.x + box.position.x && this.max.x + this.position.x >= box.min.x + box.position.x && this.min.y + this.position.y <= box.max.y + box.position.y && this.max.y + this.position.y >= box.min.y + box.position.y && this.min.z + this.position.z <= box.max.z + box.position.z && this.max.z + this.position.z >= box.min.z + box.position.z;
    }
    
    public void setPosition(final Vector3f position) {
        this.position = position;
    }
    
    public Vector3f getPosition() {
        return this.position;
    }
    
    public void setMin(final Vector3f min) {
        this.min = min;
    }
    
    public void setMax(final Vector3f max) {
        this.max = max;
    }
    
    public Vector3f getMin() {
        return this.min;
    }
    
    public Vector3f getMax() {
        return this.max;
    }
}
