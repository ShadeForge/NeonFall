// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.Scene.GUI;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import NeonFall.Manager.ResourceManager;
import NeonFall.World.Entities.Player;

public class FreeFallChargeBar
{
    private Player player;
    private static final float CHARGE_BAR_WIDTH = 0.75f;
    private static final float CHARGE_BAR_HEIGHT = 0.05f;
    private int tex_id;
    
    public FreeFallChargeBar(final Player player) {
        this.player = player;
        this.tex_id = ResourceManager.getTexture(ResourceManager.TEX_WHITE_FILE).getTextureID();
    }
    
    public void draw() {
        GL13.glActiveTexture(33984);
        GL11.glBindTexture(3553, this.tex_id);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0f, 1.0f);
        GL11.glVertex2f(-1.0f, 0.95f);
        GL11.glTexCoord2f(1.0f, 1.0f);
        GL11.glVertex2f(this.player.getFreefallCharge() / 50.0f * 0.75f - 1.0f, 0.95f);
        GL11.glTexCoord2f(1.0f, 0.0f);
        GL11.glVertex2f(this.player.getFreefallCharge() / 50.0f * 0.75f - 1.0f, 1.0f);
        GL11.glTexCoord2f(0.0f, 0.0f);
        GL11.glVertex2f(-1.0f, 1.0f);
        GL11.glEnd();
    }
}
