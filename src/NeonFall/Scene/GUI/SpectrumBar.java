package NeonFall.Scene.GUI;

import NeonFall.Manager.ResourceManager;
import NeonFall.Physics.AABB;
import NeonFall.World.Entities.TexturedEntity;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * Usage:
 * Author: lbald
 * Last Update: 09.01.2016
 */
public class SpectrumBar extends TexturedEntity {

    private AABB boundingBox;

    public SpectrumBar() {
        super(ResourceManager.PLANE_MODEL_NAME, ResourceManager.TEX_WHITE_FILE, new Vector4f(0, 1, 0, 1));
        boundingBox = new AABB(new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));
    }

    @Override
    public void update(float delta) {

    }
}
