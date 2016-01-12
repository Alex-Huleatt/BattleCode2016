package team018.units.shared;

import battlecode.common.*;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.FieldController;
import team018.frameworks.movement.Force;
import team018.frameworks.movement.MovementController;
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
    FieldController fc;
    public SoloAttack(RobotController rc)
    {
        super(rc);
        rc.setIndicatorString(0, "SoloAttack");
        RobotType type = rc.getType();
        attackRangeSquared = type.attackRadiusSquared;
        sensorRangeSquared = type.sensorRadiusSquared;
        mc = new MovementController(rc);


        fc = new FieldController(rc);
        Force stdForce = new Force(rc) {
            @Override
            public double enemy(MapLocation source, MapLocation t) {
                return -1000.0 / source.distanceSquaredTo(t);
            }
        };
        fc.addForce(stdForce, RobotType.values());

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
                    int best_dir = fc.findDir(rc.senseNearbyRobots(), new double[9]);
                    if (best_dir != -1) {
                        if (best_dir == 8) { //here is local minimum, need diff move strat.
                            for (int i = 0; i < 8; i++) {
                                if (rc.senseRubble(me.add(Common.directions[i])) > 0 && rc.isCoreReady()) {
                                    rc.clearRubble(Common.directions[i]);
                                    break;
                                }
                            }
                        } else if (!Common.isObstacle(rc, best_dir)){
                            MapLocation dest = me.add(Common.directions[best_dir]);
                            Common.basicMove(rc, dest);
                        }
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
