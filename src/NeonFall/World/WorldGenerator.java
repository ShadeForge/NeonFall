package NeonFall.World;

import NeonFall.Resources.Sounds.SpectrumSoundListener;
import NeonFall.World.Entities.BarEntity;
import NeonFall.World.Entities.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.Random;

/**
 * Usage:
 * Author: lbald
 * Last Update: 12.01.2016
 */
public class WorldGenerator {

    public final static int SPAWN_POSITION_Z = 100;
    public final static int DESPAWN_POSITION_Z = 0;
    public final static int MAX_SPAWN_TRIES = 5;

    private final static int MIN_BARENTITY_LENGTH = 5;
    private final static int MAX_BARENTITY_LENGTH = 20;
    private final static float GLOBAL_BARENTITY_SPAWN = 0.01f;
    private final static float LOCAL_BARENTITY_SPAWN = 0.05f;
    private final static int LOCAL_BARENTITY_MAX = 10;

    private RoundData roundData;
    private Random rnd;
    private SpectrumSoundListener listener;

    public WorldGenerator(RoundData roundData, SpectrumSoundListener listener) {
        this.roundData = roundData;
        this.listener = listener;
        rnd = new Random(System.nanoTime());
        new BarEntity(roundData, 100, new Vector2f((int) roundData.mapSize.x/2, (int) roundData.mapSize.y/2 + 1), listener);
    }

    public void update(float delta) {
        /*
        if(spawnable((int) roundData.mapSize.x / 2, (int) roundData.mapSize.y / 2 + 1))
            new BarEntity(roundData, 50, new Vector2f((int) roundData.mapSize.x/2, (int) roundData.mapSize.y/2 + 1), listener);

        if(spawnable((int) roundData.mapSize.x/2 + 1, (int) roundData.mapSize.y/2 + 1))
            new BarEntity(roundData, 50, new Vector2f((int) roundData.mapSize.x/2 + 1, (int) roundData.mapSize.y/2 + 1), listener);

        if(spawnable((int) roundData.mapSize.x/2 - 1, (int) roundData.mapSize.y/2 + 1))
            new BarEntity(roundData, 50, new Vector2f((int) roundData.mapSize.x/2 - 1, (int) roundData.mapSize.y/2 + 1), listener);

        if(spawnable((int) roundData.mapSize.x/2, (int) roundData.mapSize.y/2 - 1))
            new BarEntity(roundData, 50, new Vector2f((int) roundData.mapSize.x/2, (int) roundData.mapSize.y/2 - 1), listener);

        if(spawnable((int) roundData.mapSize.x/2 + 1, (int) roundData.mapSize.y/2))
            new BarEntity(roundData, 50, new Vector2f((int) roundData.mapSize.x/2 + 1, (int) roundData.mapSize.y/2), listener);

        if(spawnable((int) roundData.mapSize.x/2 - 1, (int) roundData.mapSize.y/2))
            new BarEntity(roundData, 50, new Vector2f((int) roundData.mapSize.x/2 - 1, (int) roundData.mapSize.y/2), listener);

        if(spawnable((int) roundData.mapSize.x/2 + 1, (int) roundData.mapSize.y/2 - 1))
            new BarEntity(roundData, 50, new Vector2f((int) roundData.mapSize.x/2 + 1, (int) roundData.mapSize.y/2 - 1), listener);

        if(spawnable((int) roundData.mapSize.x/2 - 1, (int) roundData.mapSize.y/2 - 1))
            new BarEntity(roundData, 50, new Vector2f((int) roundData.mapSize.x/2 - 1, (int) roundData.mapSize.y/2 - 1), listener);
*/

        if(rnd.nextFloat() <= GLOBAL_BARENTITY_SPAWN) {
            int x;
            int y;
            int tries = 0;

            do {
                x = rnd.nextInt((int) roundData.mapSize.x);
                y = rnd.nextInt((int) roundData.mapSize.y);
                tries++;
            } while(!spawnable(x, y) && tries < MAX_SPAWN_TRIES);

            if(tries < MAX_SPAWN_TRIES) {
                int length = rnd.nextInt(MAX_BARENTITY_LENGTH - MIN_BARENTITY_LENGTH) + MIN_BARENTITY_LENGTH;
                new BarEntity(roundData, length, new Vector2f(x, y), listener);
            }
        }

        if(rnd.nextFloat() <= LOCAL_BARENTITY_SPAWN) {
            Vector2f pos = roundData.player.getPosition();
            int count = 0;
            for(int x = (int)pos.x - 2; x < (int)pos.x + 2; x++) {
                for(int y = (int)pos.y - 2; y < (int)pos.y + 2; y++) {
                    if(roundData.isInMap(x, y))
                        count += roundData.barEntities[x][y].size();
                }
            }

            if(count < LOCAL_BARENTITY_MAX) {
                int x;
                int y;
                int tries = 0;

                do {
                    x = rnd.nextInt(5) + (int)pos.x - 2;
                    y = rnd.nextInt(5) + (int)pos.y - 2;
                    tries++;
                } while(!(roundData.isInMap(x, y) && spawnable(x, y)) && tries < MAX_SPAWN_TRIES);

                if(tries < MAX_SPAWN_TRIES) {
                    int length = rnd.nextInt(MAX_BARENTITY_LENGTH - MIN_BARENTITY_LENGTH) + MIN_BARENTITY_LENGTH;
                    new BarEntity(roundData, length, new Vector2f(x, y), listener);
                }
            }
        }
    }

    private boolean spawnable(int x, int y) {

        if (roundData.barEntities[x][y].size() == 0)
            return true;

        BarEntity entity = roundData.barEntities[x][y].getLast();
        Vector3f pos = entity.getPosition();
        int l = entity.getLength();

        if (pos.z + l * 2f < SPAWN_POSITION_Z)
            return true;

        return false;
    }
}
