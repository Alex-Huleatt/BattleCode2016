package team018.units.archon;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team018.frameworks.moods.Mood;

/**
 * Created by Todd on 1/8/2016.
 *
 * The purpose of this Mood is to force specific initial spawns
 * before letting it pick randomly
 *
 */
public class ArchonInitial extends ArchonDefault
{
    static RobotType[] spawnOrder = {
            RobotType.SCOUT,
            RobotType.VIPER,
            RobotType.GUARD,
    };

    int spawnPointer = 0;

    public ArchonInitial(RobotController rc)
    {
        super (rc);
    }

    @Override
    public void act() throws Exception
    {
        if (ready && broadcastLocation())
        {
            broadcastLocation();

            if (!buildRobot(spawnOrder[spawnPointer]))
            {
                //  should never cause an error as long as act is after swing check
                spawnPointer++;
            }
        }
    }



    @Override
    public Mood swing ()
    {
        if (spawnPointer == spawnOrder.length)
        {
            return new ArchonDefault(rc);
        }
        return null;
    }
}
