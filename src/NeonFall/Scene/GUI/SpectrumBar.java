// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Scene.GUI;

import org.joml.Vector3f;
import org.joml.Vector4f;
import NeonFall.Manager.ResourceManager;
import NeonFall.Physics.AABB;
import NeonFall.World.Entities.TexturedEntity;

public class SpectrumBar extends TexturedEntity
{
    private AABB boundingBox;
    
    public SpectrumBar() {
        super("Plane_Model", ResourceManager.TEX_WHITE_FILE, new Vector4f(0.0f, 1.0f, 0.0f, 1.0f));
        this.boundingBox = new AABB(new Vector3f(-1.0f, -1.0f, -1.0f), new Vector3f(1.0f, 1.0f, 1.0f));
    }
    
    @Override
    public void update(final float delta) {
    }
}
