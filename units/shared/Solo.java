package team018.units.shared;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.comm.SignalType;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.FieldController;
import team018.frameworks.movement.MovementController;
import team018.frameworks.util.Common;

import java.util.HashMap;

/**
 * Created by alexhuleatt on 1/4/16.
 *
 * Update by Todd - Guards and Soldiers should both be able to use this
 */
public class Solo extends Mood
{

    int attackRangeSquared, // unused
        sensorRangeSquared;
    MovementController mc;
    RobotInfo[] hostile;
    FieldController fc;
    int broadcast_cd;
    Comm c;
    MapLocation halpLocation = null;
    HashMap<Integer, MapLocation> archon_positions;
    int halp_cd;

    public Solo(RobotController rc)
    {
        super(rc);
        rc.setIndicatorString(0, "SoloAttack");
        RobotType type = rc.getType();
        attackRangeSquared = type.attackRadiusSquared;
        sensorRangeSquared = type.sensorRadiusSquared;
        mc = new MovementController(rc);


        fc = new FieldController(rc);
        fc.can_fly=true;
        broadcast_cd=0;
        c = new Comm(rc);
        halp_cd = 0;
        archon_positions=new HashMap<>();

    }

    @Override
    public void update() {
        super.update();
        hostile=rc.senseHostileRobots(me, sensorRangeSquared);
        broadcast_cd--;
        SignalInfo si;

        while ((si = c.receiveSignal()) != null) {

            if (si.type == SignalType.ARCHON_LOC) {
                archon_positions.put(si.robotID, si.senderLoc);
            }

            if (si.type == SignalType.HALP && (halpLocation==null || halp_cd <=0 || si.senderLoc.distanceSquaredTo(me)< halpLocation.distanceSquaredTo(me)))
            {
                halpLocation = si.senderLoc;
                halp_cd = 100;
            }
        }
        halp_cd--;
    }

    @Override
    public void act() throws Exception
    {
        if (broadcast_cd<=0){
            rc.broadcastSignal(300);
            broadcast_cd=10;
        }

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
            int ally_threat = Common.getThreat(rc.senseNearbyRobots(rc.getType().sensorRadiusSquared, rc.getTeam()));
            int enemy_threat = Common.getThreat(hostile);
            if (ally_threat > enemy_threat && closest != null && rc.canAttackLocation(closestLocation)) {
                rc.attackLocation(closestLocation);
            } else {
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
                    } else {
                        rc.clearRubble(Common.directions[best_dir]);
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
                int dist = me.add(Common.directions[i]).distanceSquaredTo(ri.location);
                if (dist!=0)costs[i] += -1000 / dist * force_mult;
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
        if (halpLocation!=null && me.distanceSquaredTo(halpLocation)>40) {
            return new Halper(rc, halpLocation);
        }
        return null;
    }
}
