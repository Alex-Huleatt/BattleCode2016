package team018.units.shared;

import battlecode.common.*;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.MovementController;
import team018.frameworks.movement.Potential;
import team018.frameworks.util.Common;

/**
 * Created by alexhuleatt on 1/4/16.
 *
 * Update by Todd - Guards and Soldiers should both be able to use this
 */
public class SoloAttack extends Mood
{

    int attackRangeSquared, // unused
        sensorRangeSquared;
    MovementController mc;
    RobotInfo[] hostile;
    Potential p;
    public SoloAttack(RobotController rc)
    {
        super(rc);
        RobotType type = rc.getType();
        attackRangeSquared = type.attackRadiusSquared;
        sensorRangeSquared = type.sensorRadiusSquared;
        mc = new MovementController(rc);

        int numTypes = RobotType.values().length;
        double[] en_costs = new double[numTypes];
        double[] al_costs = new double[numTypes];
        en_costs[RobotType.SOLDIER.ordinal()] = -1000;
        en_costs[RobotType.ARCHON.ordinal()] = -1000;
        en_costs[RobotType.GUARD.ordinal()] = -1000;
        en_costs[RobotType.ZOMBIEDEN.ordinal()] = -1000;
        en_costs[RobotType.STANDARDZOMBIE.ordinal()] = -1000;
        en_costs[RobotType.FASTZOMBIE.ordinal()] = -1000;
        en_costs[RobotType.BIGZOMBIE.ordinal()] = -1000;
        en_costs[RobotType.VIPER.ordinal()] = -1000;
        en_costs[RobotType.TURRET.ordinal()] = -1000;

        p = new Potential(rc, en_costs,al_costs,true);

    }

    @Override
    public void update() {
        super.update();
        hostile=rc.senseHostileRobots(me, sensorRangeSquared);
    }

    @Override
    public void act() throws Exception
    {

        // If it can attack, try!
        if (rc.isCoreReady() && rc.isWeaponReady())
        {
            RobotInfo closest = null;
            MapLocation closestLocation = null;
            MapLocation checkLocation;
            int distance = Integer.MAX_VALUE, checkDistance, x, y;
            for (int i = 0; i < hostile.length; i++)
            {
                checkLocation = hostile[i].location;
                checkDistance = me.distanceSquaredTo(checkLocation);
                if (checkDistance < distance)
                {
                    closest = hostile[i];
                    closestLocation = checkLocation;
                    distance = checkDistance;
                }
            }
            // It found nothing. Go back to rally point
            if (closest == null)
            {
                System.out.println("what");
                //  TODO
            }
            // It found targets
            else
            {
                if (rc.canAttackLocation(closestLocation))
                {
                    rc.attackLocation(closestLocation);
                }
                else
                {
                    MapLocation best = p.findMin(new double[8]);
                    if (best != null) {
                        Common.basicMove(rc,best);
                    }
                }
            }
        }
    }


    @Override
    public Mood swing()
    {
        if (0 == hostile.length)
        {
            return new Standby(rc);
        }
        return null;
    }
}
