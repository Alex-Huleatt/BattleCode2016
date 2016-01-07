package team018.units.archon;

import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team018.frameworks.moods.Mood;

import java.util.Random;

/**
 * Created by alexhuleatt on 1/4/16.
 * <p>
 * Update by Todd: Attempts to spawn a random unit based on a simple probability check.
 * Should spawn in a random direction.
 */
public class ArchonDefault extends Mood
{
    Random rand;
    static final Direction[] directions = Direction.values();

    //	The highest-to-lowest preferences of robots to build
    static final RobotType[] robotPriorities = {
            RobotType.SCOUT,
            RobotType.GUARD,
            RobotType.VIPER,
            RobotType.TURRET,
            RobotType.SOLDIER
    };
    //	The likeliness of building each robot type out of 128
    static final int[] priorityLevels = {
            0,
            32,
            32,
            32,
            32
    };
    RobotType lastSpawned, toSpawn;

    boolean ready;

    public ArchonDefault(RobotController rc)
    {
        super(rc);
        rand = new Random(rc.getID());
    }

    @Override
    public void update()
    {
        super.update();

        ready = rc.isCoreReady();
    }

    @Override
    public void act() throws Exception
    {
        if (ready)
        {


            //Building logic
            lastSpawned = toSpawn;

            //	Determine where to spawn next
            int next = rand.nextInt() & 127,
                    i = 0;
            while (-1 < next)
            {
                if (next < priorityLevels[i])
                {
                    toSpawn = robotPriorities[i];
                    break;
                }
                next -= priorityLevels[i++];
            }

            //	Build. Goes in order of direction looking for place to build
            for (Direction d : directions)
            {
                if (rc.hasBuildRequirements(toSpawn) && rc.canBuild(d, toSpawn))
                {
                    rc.build(d, toSpawn);
                    break;
                }
            }
        }
    }

    @Override
    public Mood swing()
    {
        return null;
    }
}
