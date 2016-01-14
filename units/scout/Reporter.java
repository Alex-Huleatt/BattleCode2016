package team018.units.scout;

import battlecode.common.*;
import team018.frameworks.comm.Comm;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.FieldController;
import team018.frameworks.movement.Force;
import team018.frameworks.util.Common;

/**
 * Created by Todd on 1/13/2016.
 */
public class Reporter extends Mood
{
    Comm c;
    Team us;
    FieldController fc;
    RobotInfo[] hostile;
    RobotInfo[] friendly;

    public Reporter(RobotController rc)
    {
        super(rc);
        us = rc.getTeam();
        fc = new FieldController(rc);
        c = new Comm(rc);
    }

    protected double[] init_costs()
    {
        double[] costs = new double[8];
        for (int i = 0; i < 8; i++)
        {

        }
        return costs;
    }

    @Override
    public void update()// throws Exception
    {
        super.update();

        friendly = rc.senseNearbyRobots(RobotType.SCOUT.sensorRadiusSquared, us);
        hostile = rc.senseHostileRobots(me, RobotType.SCOUT.sensorRadiusSquared);
    }

    @Override
    public void act() throws Exception
    {
        RobotInfo target = null;
        int distance = Integer.MIN_VALUE, checkDistance,
            priority = Integer.MIN_VALUE, checkPriority;
        MapLocation bestLocation = null, checkLocation;
        for (RobotInfo enemy: hostile)
        {
            checkLocation = enemy.location;
            checkDistance = me.distanceSquaredTo(checkLocation);
            checkPriority = Common.get
            if ()
        }
    }
}
