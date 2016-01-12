package team018.units.shared;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.FieldController;
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
            RobotInfo den = null;
            MapLocation closestLocation = null;
            MapLocation checkLocation;
            int distance = Integer.MAX_VALUE, checkDistance, x, y;
            for (int i = 0; i < hostile.length; i++)
            {
                //  Dens get special behavior
                //  Only attack them if there are no enemies around
                if (hostile[i].type == RobotType.ZOMBIEDEN)
                {
                    den = hostile[i];
                }
                else
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
            }
            // It found only a den
            if (den != null && closest == null)
            {
                closest = den;
                closestLocation = den.location;
            }
            // It found targets
            if (closest != null)
            {
                if (rc.canAttackLocation(closestLocation))
                {
                    rc.attackLocation(closestLocation);
                }
                else
                {
                    int best_dir = fc.findDir(rc.senseNearbyRobots(), init_costs());
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

    public double[] init_costs() throws Exception{
        double[] costs = new double[9];
        RobotInfo[] allies = rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam());
        int ally_threat = Common.getThreat(allies);
        int enemy_threat = Common.getThreat(hostile);
        double force_mult;
        if (ally_threat > enemy_threat) {
            force_mult = 1;
        } else {
            force_mult = -1;
        }

        for (RobotInfo ri : hostile) {

            for (int i = 0; i < 8; i++) {
                costs[i] += -1000 / me.add(Common.directions[i]).distanceSquaredTo(ri.location) * force_mult;
            }
            costs[8] +=  -1000 / me.distanceSquaredTo(ri.location) * force_mult;
        }


        return costs;
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
