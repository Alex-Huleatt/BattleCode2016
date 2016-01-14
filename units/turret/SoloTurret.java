package team018.units.turret;

import battlecode.common.*;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalType;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.MovementController;

/**
 * Created by alexhuleatt on 1/4/16.
 *
 */
public class SoloTurret extends Mood
{
    Comm c;
    int attackRangeSquared, // unused
        sensorRangeSquared,
        attackRangeMin;
    MovementController mc;
    RobotInfo[] hostile;
    MapLocation target = null;

    public SoloTurret(RobotController rc)
    {
        super(rc);
        c = new Comm(rc);
        attackRangeSquared = RobotType.TURRET.attackRadiusSquared;
        sensorRangeSquared = RobotType.TURRET.sensorRadiusSquared;
        attackRangeMin  = GameConstants.TURRET_MINIMUM_RANGE;
        mc = new MovementController(rc);
    }

    @Override
    public void update() {
        super.update();

        SignalInfo si;

        while ((si = c.receiveSignal()) != null) {

            if (si.type == SignalType.ATTACK && si.data < 6)
            {
                target = si.targetLoc;
                //  swing, because now we want to attack together
                return;
            }
        }

        hostile = rc.senseHostileRobots(me, sensorRangeSquared);
    }

    @Override
    public void act() throws Exception
    {

        rc.setIndicatorString(0, "Solo");


        // If it can attack, try!
        if (rc.isCoreReady() && rc.isWeaponReady())
        {
            RobotInfo closest = null;
            MapLocation closestLocation = null, checkLocation;
            int distance = Integer.MAX_VALUE, checkDistance, x, y;
            for (int i = 0; i < hostile.length; i++)
            {
                checkLocation = hostile[i].location;
                x = me.x - checkLocation.x;
                y = me.y - checkLocation.y;
                checkDistance = x * x + y * y;
                //  Turrets have a minimum attacking range, so don't bother
                //  with any enemies that might be closer
                if (checkDistance < distance && attackRangeMin <= distance)
                {
                    closest = hostile[i];
                    closestLocation = checkLocation;
                    distance = checkDistance;
                }
            }
            if (closest != null)
            {
                if (rc.canAttackLocation(closestLocation))
                {
                    rc.attackLocation(closestLocation);
                }
            }
            //  Turrets can't move, so there's nothing else to do in SoloTurret mode.
        }
    }


    @Override
    public Mood swing()
    {
        if (target != null)
        {
            return new TeamTurret(rc, target);
        }
        return null;
    }
}
