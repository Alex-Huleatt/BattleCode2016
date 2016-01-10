package team018.units.shared;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.comm.SignalType;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.MovementController;
import team018.frameworks.movement.Potential;
import team018.frameworks.util.Common;

import java.util.HashMap;

/**
 * Created by alexhuleatt on 1/4/16.
 *
 * Update by Todd - Guards and Soldiers should both be able to use this
 */
public class Standby extends Mood
{
    int sensorRangeSquared;
    MovementController mc;
    RobotInfo[] hostile;
    Potential p;
    HashMap<Integer, MapLocation> archon_positions;
    Comm c;
    int avgX,avgY, moves;
    int last_dir;
    public Standby(RobotController rc)
    {
        super(rc);
        sensorRangeSquared = rc.getType().sensorRadiusSquared;
        mc = new MovementController(rc);

        double[] en_costs = new double[RobotType.values().length];
        double[] al_costs = new double[RobotType.values().length];
        p = new Potential(rc, en_costs, al_costs, 0);
        c = new Comm(rc);
        me = rc.getLocation();
        avgX = me.x;
        avgY = me.y;
        moves = 1;
        archon_positions=new HashMap<>();
    }

    @Override
    public void update() {
        super.update();
        hostile = rc.senseHostileRobots(me, sensorRangeSquared);

    }

    @Override
    public void act() throws Exception
    {
        rc.setIndicatorString(0,"Standby");
        if (rc.isCoreReady()) {
            SignalInfo si;
            while ((si = c.receiveSignal()) != null) {

                if (si.type == SignalType.ARCHON_LOC) {
                    archon_positions.put(si.robotID, si.senderLoc);
                }
            }
            double[] init_costs = init_costs();
            MapLocation best = p.findMin(init_costs);

            if (best != null) {
                if (rc.senseRubble(best)!= 0) {
                    rc.clearRubble(me.directionTo(best));
                } else {
                    last_dir = Common.dirToInt(me.directionTo(best));
                    Common.basicMove(rc, best);
                }
            }
        }
    }


    private double[] init_costs() throws Exception {
        //want to do a non-linear cost to stay at specific distance from archon.

        double[] costs = new double[8];
        MapLocation nearest=null;
        int distsqr=0;
        for (MapLocation m : archon_positions.values()) {
            if (nearest==null || m.distanceSquaredTo(me) < distsqr) {
                nearest=m;
                distsqr=m.distanceSquaredTo(me);
            }
        }
        for (int i = 0; i < 8; i++) {
            MapLocation t = me.add(Common.directions[i]);

                if (nearest != null) {
                    double x = 100 * ((t.distanceSquaredTo(nearest)-35)/100);
                    costs[i] += 1000.0 * x * x;
                    int vx1 = me.x-nearest.x;
                    int vy1 = me.y-nearest.y;

                    int vx2 = t.x-nearest.x;
                    int vy2 = t.y-nearest.y;

                    if (vy1*vx2 > vx1*vy2) costs[i]+= 1000;
            }
        }
        return costs;
    }

    @Override
    public Mood swing()
    {
        //  Can save the result to prevent redundant calculations
        if (0 < hostile.length)
        {
            return new SoloAttack(rc);
        }
        return null;
    }
}
