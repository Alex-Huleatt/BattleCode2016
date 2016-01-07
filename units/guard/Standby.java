package team018.units.guard;

import battlecode.common.*;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.MovementController;

/**
 * Created by alexhuleatt on 1/4/16.
 *
 * Update by Todd
 */
public class Standby extends Mood
{
    MapLocation myLocation;
    int sensorRangeSquared;
    MovementController mc;


    public Standby(RobotController rc)
    {
        super(rc);
        sensorRangeSquared = RobotType.GUARD.sensorRadiusSquared;
        mc = new MovementController(rc);
    }

    public void update()
    {
        myLocation = rc.getLocation();
    }

    @Override
    public void act() throws Exception
    {
        //  todo: 1-2 squares away from an archon
    }

    @Override
    public Mood swing()
    {
        //  Can save the result to prevent redundant calculations
        RobotInfo[] robots = rc.senseHostileRobots(myLocation, sensorRangeSquared);
        if (0 < robots.length)
        {
            return new SoloAttack(rc);
        }
        return null;
    }
}
