package team018.units.scout;

import battlecode.common.*;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.comm.SignalType;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.FieldController;
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

        hostile = rc.senseHostileRobots(me, RobotType.SCOUT.sensorRadiusSquared);
    }

    @Override
    public void act() throws Exception
    {
        int distance = Integer.MIN_VALUE, checkDistance,
            priority = Integer.MIN_VALUE, checkPriority;
        MapLocation location = null, checkLocation;

        //  Find the closest enemy with highest priority
        for (RobotInfo enemy: hostile)
        {
            checkLocation = enemy.location;
            checkDistance = me.distanceSquaredTo(checkLocation);
            checkPriority = Common.getAttackPriority(enemy);
            if (priority < checkPriority || checkDistance < distance)
            {
                location = checkLocation;
                distance = checkDistance;
                priority = checkPriority;
            }
        }


        //  If something was found, report it
        if (location != null)
        {
            SignalInfo si = new SignalInfo();
            si.targetLoc = location;
            si.type = SignalType.ATTACK;
            si.data = rc.getRoundNum();
            c.sendSignal(si, 100);
        }
    }
}
