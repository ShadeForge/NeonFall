package NeonFall.World.Entities;

import NeonFall.Manager.ResourceManager;
import NeonFall.Physics.AABB;
import NeonFall.Resources.Sounds.SpectrumSoundListener;
import NeonFall.World.RoundData;
import NeonFall.World.WorldGenerator;
import org.joml.*;

import java.util.Random;

/**
 * Usage:
 * Author: lbald
 * Last Update: 12.01.2016
 */
public class BarEntity extends TexturedEntity {

    private final static float WORLD_MOVE_SPEED = 25f;
    private final static float MIN_LIGHT_STRENGTH = 0.2f;
    private final static float MAX_LIGHT_STRENGTH = 0.5f;
    private final static float GLOW_SPEED = 1f;

    private AABB boundingBox;
    private int length;
    private Vector3f position;
    private RoundData roundData;
    private SpectrumSoundListener listener;
    private float currentGlowStrength = MIN_LIGHT_STRENGTH;
    private float currentGlowSpeed = GLOW_SPEED;

    public BarEntity(RoundData roundData, int length, Vector2f position, SpectrumSoundListener listener) {
        super(ResourceManager.CUBE_MODEL_FILE, ResourceManager.TEX_CUBE_FILE, randomColor());

        this.position = new Vector3f(position.x, position.y, WorldGenerator.SPAWN_POSITION_Z + length / 2);
        modelMatrix = new Matrix4f().translate(this.position.x, this.position.y, this.position.z);
        modelMatrix.scale(0.5f, 0.5f, length);
        Matrix3f mat = new Matrix3f().scale(0.5f, 0.5f, length);
        boundingBox = new AABB(mat.transform(new Vector3f(-1, -1, -1)),
                mat.transform(new Vector3f(1, 1, 1)));
        this.listener = listener;
        this.roundData = roundData;
        this.length = length;
        boundingBox.setPosition(this.position);
        this.roundData.barEntities[(int)position.x][(int)position.y].add(this);
        update(0);
    }

    @Override
    public void update(float delta) {
        position.z -= delta * WORLD_MOVE_SPEED;
        this.modelMatrix = new Matrix4f().translate(position.x, position.y, position.z).scale(0.5f, 0.5f, length);

        if(position.z + length < WorldGenerator.DESPAWN_POSITION_Z)
            roundData.removing.add(this);

        currentGlowStrength += delta * currentGlowSpeed;

        if(currentGlowStrength > MAX_LIGHT_STRENGTH) {
            currentGlowStrength = MAX_LIGHT_STRENGTH;
            currentGlowSpeed*=-1;
        } else if(currentGlowStrength < MIN_LIGHT_STRENGTH) {
            currentGlowStrength = MIN_LIGHT_STRENGTH;
            currentGlowSpeed*=-1;
        }
    }

    public boolean isColliding(AABB box) {
        return boundingBox.intersectAABB(box);
    }

    @Override
    public Vector4f getColor() {
        return new Vector4f(color).mul(currentGlowStrength);
    }

    public Vector3f getPosition() {
        return position;
    }

    public int getLength() {
        return length;
    }

    private static Vector4f randomColor() {

        Vector4f color;
        int rand = new Random().nextInt(3);

        switch(rand) {
            case 0:
                color = new Vector4f(0, 1, 0, 1);
                break;
            case 1:
                color = new Vector4f(1, 1, 0, 1);
                break;
            default:
                color = new Vector4f(0, 1, 1, 1);
                break;
        }

        return color;
    }
}
