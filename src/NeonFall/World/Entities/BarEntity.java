// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.World.Entities;

import java.util.Random;
import org.joml.Vector4f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import NeonFall.Manager.ResourceManager;
import org.joml.Vector2f;
import NeonFall.Resources.Sounds.SpectrumSoundListener;
import NeonFall.World.RoundData;
import org.joml.Vector3f;
import NeonFall.Physics.AABB;

public class BarEntity extends TexturedEntity
{
    private static final float WORLD_MOVE_SPEED = 25.0f;
    private static final float MIN_LIGHT_STRENGTH = 0.2f;
    private static final float MAX_LIGHT_STRENGTH = 0.5f;
    private static final float GLOW_SPEED = 1.0f;
    private AABB boundingBox;
    private int length;
    private Vector3f position;
    private RoundData roundData;
    private SpectrumSoundListener listener;
    private float currentGlowStrength;
    private float currentGlowSpeed;
    
    public BarEntity(final RoundData roundData, final int length, final Vector2f position, final SpectrumSoundListener listener) {
        super(ResourceManager.CUBE_MODEL_FILE, ResourceManager.TEX_CUBE_FILE, randomColor());
        this.currentGlowStrength = 0.2f;
        this.currentGlowSpeed = 1.0f;
        this.position = new Vector3f(position.x, position.y, (float)(100 + length / 2));
        (this.modelMatrix = new Matrix4f().translate(this.position.x, this.position.y, this.position.z)).scale(0.5f, 0.5f, (float)length);
        final Matrix3f mat = new Matrix3f().scale(0.5f, 0.5f, (float)length);
        this.boundingBox = new AABB(mat.transform(new Vector3f(-1.0f, -1.0f, -1.0f)), mat.transform(new Vector3f(1.0f, 1.0f, 1.0f)));
        this.listener = listener;
        this.roundData = roundData;
        this.length = length;
        this.boundingBox.setPosition(this.position);
        this.roundData.barEntities[(int)position.x][(int)position.y].add(this);
        this.update(0.0f);
    }
    
    @Override
    public void update(final float delta) {
        final Vector3f position = this.position;
        position.z -= delta * 25.0f;
        this.modelMatrix = new Matrix4f().translate(this.position.x, this.position.y, this.position.z).scale(0.5f, 0.5f, (float)this.length);
        if (this.position.z + this.length < 0.0f) {
            this.roundData.removing.add(this);
        }
        this.currentGlowStrength += delta * this.currentGlowSpeed;
        if (this.currentGlowStrength > 0.5f) {
            this.currentGlowStrength = 0.5f;
            this.currentGlowSpeed *= -1.0f;
        }
        else if (this.currentGlowStrength < 0.2f) {
            this.currentGlowStrength = 0.2f;
            this.currentGlowSpeed *= -1.0f;
        }
    }
    
    public boolean isColliding(final AABB box) {
        return this.boundingBox.intersectAABB(box);
    }
    
    @Override
    public Vector4f getColor() {
        return new Vector4f(this.color).mul(this.currentGlowStrength);
    }
    
    public Vector3f getPosition() {
        return this.position;
    }
    
    public int getLength() {
        return this.length;
    }
    
    private static Vector4f randomColor() {
        final int rand = new Random().nextInt(3);
        Vector4f color = null;
        switch (rand) {
            case 0: {
                color = new Vector4f(0.0f, 1.0f, 0.0f, 1.0f);
                break;
            }
            case 1: {
                color = new Vector4f(1.0f, 1.0f, 0.0f, 1.0f);
                break;
            }
            default: {
                color = new Vector4f(0.0f, 1.0f, 1.0f, 1.0f);
                break;
            }
        }
        return color;
    }
}
