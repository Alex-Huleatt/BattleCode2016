package team018.units.shared;

import battlecode.common.*;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.MovementController;

/**
 * Created by alexhuleatt on 1/4/16.
 *
 * Update by Todd - Guards and Soldiers should both be able to use this
 */
public class Standby extends Mood
{
    MapLocation myLocation;
    int sensorRangeSquared;
    MovementController mc;


    public Standby(RobotController rc)
    {
        super(rc);
        sensorRangeSquared = rc.getType().sensorRadiusSquared;
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
