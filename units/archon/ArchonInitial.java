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
    static final RobotType[] spawnOrder = {
            RobotType.SCOUT
    };

    int spawnPointer = 0;

    public ArchonInitial(RobotController rc)
    {
        super (rc);
    }

    @Override
    public void act() throws Exception
    {
        rc.setIndicatorString(0,"Initial");
        if (ready && broadcastLocation())
        {

            if (buildRobot(spawnOrder[spawnPointer]))
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
        if (rc.senseHostileRobots(me, rc.getType().sensorRadiusSquared).length > 0) {
            return new Spooked(rc);
        }
        return null;
    }
}
