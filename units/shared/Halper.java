package team018.units.shared;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
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
public class Halper extends Mood
{
    int sensorRangeSquared;
    MovementController mc;
    RobotInfo[] hostile;
    FieldController fc;
    HashMap<Integer, MapLocation> archon_positions;
    Comm c;
    int avgX,avgY, moves;
    int last_dir;
    MapLocation halpLocation;

    public Halper(RobotController rc, MapLocation halpLocation)
    {
        super(rc);
        rc.setIndicatorString(0, "Halper");
        this.halpLocation = halpLocation;
        sensorRangeSquared = rc.getType().sensorRadiusSquared;
        mc = new MovementController(rc);
        fc = new FieldController(rc);
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
        if (rc.isCoreReady()) {
            SignalInfo si;
            while ((si = c.receiveSignal()) != null) {

                if (si.type == SignalType.ARCHON_LOC) {
                    archon_positions.put(si.robotID, si.senderLoc);
                }

                if (si.type == SignalType.HALP)
                {
                    MapLocation newLoc = si.senderLoc;
                    if (me.distanceSquaredTo(newLoc) < me.distanceSquaredTo(halpLocation))
                    {
                        halpLocation = newLoc;
                    }
                }
            }
            double[] init_costs = init_costs();
            int best_dir = fc.findDir(rc.senseNearbyRobots(),init_costs);
            rc.setIndicatorString(1, best_dir+"");
            if (best_dir != -1)
            {
                MapLocation dest = me.add(Common.directions[best_dir]);
                if (!Common.isObstacle(rc, best_dir))
                {
                    rc.setIndicatorString(1, "wanna move to " + dest);
                    Common.basicMove(rc, dest);
                }
            } else {
                rc.setIndicatorString(1, "dun wanna move");
            }
        }
    }


    private double[] init_costs() throws Exception {
        //want to do a non-linear cost to stay at specific distance from archon.

        double[] costs = new double[8];
        //System.out.println(nearest);
        for (int i = 0; i < 8; i++) {
            MapLocation t = me.add(Common.directions[i]);

            if (halpLocation != null) {
                double x =  25*((t.distanceSquaredTo(halpLocation)-10)/25);// round to nearest 25 to smooth the costs
                costs[i] += 1000.0 * x * x;
                int vx1 = me.x-halpLocation.x;
                int vy1 = me.y-halpLocation.y;

                int vx2 = t.x-halpLocation.x;
                int vy2 = t.y-halpLocation.y;

                if (vy1*vx2 > vx1*vy2) costs[i]+= 3000; //Cross product thing. checks clockwise or counter.
            }
        }
        return costs;
    }

    @Override
    public Mood swing()
    {
        if (me.distanceSquaredTo(halpLocation) < 15)
        {
            return new Standby(rc);
        }
        if (0 < hostile.length)
        {
            return new SoloAttack(rc);
        }
        return null;
    }
}
