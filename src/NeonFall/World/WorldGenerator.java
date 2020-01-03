// 
// Decompiled by Procyon v0.5.36
// 

package NeonFall.World;

import org.joml.Vector3f;
import org.joml.Vector2f;
import NeonFall.World.Entities.BarEntity;
import NeonFall.Resources.Sounds.SpectrumSoundListener;
import java.util.Random;

public class WorldGenerator
{
    public static final int SPAWN_POSITION_Z = 100;
    public static final int DESPAWN_POSITION_Z = 0;
    public static final int MAX_SPAWN_TRIES = 5;
    private static final int MIN_BARENTITY_LENGTH = 5;
    private static final int MAX_BARENTITY_LENGTH = 20;
    private static final float GLOBAL_BARENTITY_SPAWN = 0.01f;
    private static final float LOCAL_BARENTITY_SPAWN = 0.05f;
    private static final int LOCAL_BARENTITY_MAX = 10;
    private RoundData roundData;
    private Random rnd;
    private SpectrumSoundListener listener;
    
    public WorldGenerator(final RoundData roundData, final SpectrumSoundListener listener) {
        this.roundData = roundData;
        this.listener = listener;
        this.rnd = new Random(System.nanoTime());
        new BarEntity(roundData, 100, new Vector2f((float)((int)roundData.mapSize.x / 2), (float)((int)roundData.mapSize.y / 2 + 1)), listener);
    }
    
    public void update(final float delta) {
        if (this.rnd.nextFloat() <= 0.01f) {
            int tries = 0;
            int x;
            int y;
            do {
                x = this.rnd.nextInt((int)this.roundData.mapSize.x);
                y = this.rnd.nextInt((int)this.roundData.mapSize.y);
                ++tries;
            } while (!this.spawnable(x, y) && tries < 5);
            if (tries < 5) {
                final int length = this.rnd.nextInt(15) + 5;
                new BarEntity(this.roundData, length, new Vector2f((float)x, (float)y), this.listener);
            }
        }
        if (this.rnd.nextFloat() <= 0.05f) {
            final Vector2f pos = this.roundData.player.getPosition();
            int count = 0;
            for (int x2 = (int)pos.x - 2; x2 < (int)pos.x + 2; ++x2) {
                for (int y2 = (int)pos.y - 2; y2 < (int)pos.y + 2; ++y2) {
                    if (this.roundData.isInMap(x2, y2)) {
                        count += this.roundData.barEntities[x2][y2].size();
                    }
                }
            }
            if (count < 10) {
                int tries2 = 0;
                int x2;
                int y2;
                do {
                    x2 = this.rnd.nextInt(5) + (int)pos.x - 2;
                    y2 = this.rnd.nextInt(5) + (int)pos.y - 2;
                    ++tries2;
                } while ((!this.roundData.isInMap(x2, y2) || !this.spawnable(x2, y2)) && tries2 < 5);
                if (tries2 < 5) {
                    final int length2 = this.rnd.nextInt(15) + 5;
                    new BarEntity(this.roundData, length2, new Vector2f((float)x2, (float)y2), this.listener);
                }
            }
        }
    }
    
    private boolean spawnable(final int x, final int y) {
        if (this.roundData.barEntities[x][y].size() == 0) {
            return true;
        }
        final BarEntity entity = this.roundData.barEntities[x][y].getLast();
        final Vector3f pos = entity.getPosition();
        final int l = entity.getLength();
        return pos.z + l * 2.0f < 100.0f;
    }
}
