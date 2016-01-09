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
    public Standby(RobotController rc)
    {
        super(rc);
        sensorRangeSquared = rc.getType().sensorRadiusSquared;
        mc = new MovementController(rc);

        double[] en_costs = new double[RobotType.values().length];
        double[] al_costs = new double[RobotType.values().length];
        p = new Potential(rc, en_costs, al_costs, 1.0);
        c = new Comm(rc);
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
        if (rc.isCoreReady()) {
            SignalInfo si;
            while ((si = c.receiveSignal()) != null) {

                if (si.type == SignalType.ARCHON_LOC) {
                    archon_positions.put(si.robotID, si.senderLoc);
                }
            }

            Common.basicMove(rc,p.findMin(init_costs()));

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
            if (Common.isObstacle(rc,t)) {
                costs[i] = Double.POSITIVE_INFINITY;
            } else {
                if (nearest != null) {
                    double x = 1.0 / (t.distanceSquaredTo(nearest)-30);
                    costs[i] += 10000 * x;
                }
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
