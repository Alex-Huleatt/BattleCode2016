package team018.units.scout;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.comm.SignalType;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.Potential;
import team018.frameworks.util.Common;

import java.util.BitSet;


/**
 * Created by alexhuleatt on 1/6/16.
 */
public class Explore extends Mood {

    int avgX;
    int avgY;
    int moves;
    Potential p;
    BitSet visited;
    Comm c;

    RobotInfo[] nearby;
    public Explore(RobotController rc) {
        super(rc);
        me = rc.getLocation();
        avgX=me.x;
        avgY=me.y;
        moves = 1;
        int numTypes = RobotType.values().length;
        double[] en_costs = new double[numTypes];
        double[] al_costs = new double[numTypes];

        en_costs[RobotType.SOLDIER.ordinal()] = 1000;
        en_costs[RobotType.ARCHON.ordinal()] = 1000;
        en_costs[RobotType.GUARD.ordinal()] = 1000;
        en_costs[RobotType.ZOMBIEDEN.ordinal()] = 1000;
        en_costs[RobotType.STANDARDZOMBIE.ordinal()] = 1000;
        en_costs[RobotType.FASTZOMBIE.ordinal()] = 1000;
        en_costs[RobotType.BIGZOMBIE.ordinal()] = 1000;
        en_costs[RobotType.VIPER.ordinal()] = 1000;
        en_costs[RobotType.TURRET.ordinal()] = 1000;


        p = new Potential(rc, en_costs, al_costs, 0.0);
        visited = new BitSet();
        this.c = new Comm(rc);
    }

    @Override
    public void update() {
        super.update();
        nearby = rc.senseNearbyRobots();
    }

    @Override
    public void act() throws Exception {
        visited.set(me.hashCode());

        if (rc.isCoreReady()) {
            double[] adj_costs = new double[8];
            MapLocation avg = new MapLocation(avgX / moves, avgY / moves);
            MapLocation t;
            for (int i = 0; i < 8; i++) {
                t = me.add(Common.directions[i]);
                adj_costs[i] = -2 * t.distanceSquaredTo(avg);
                if (visited.get(t.hashCode())) {
                    adj_costs[i] += 100;
                }
                if (rc.senseParts(t) > 0) {
                    SignalInfo s = new SignalInfo();
                }
            }
            MapLocation best = p.findMin(adj_costs);
            if (best != null) {
                avgX += best.x;
                avgY += best.y;
                moves++;
                if (moves > 30) {
                    avgX = me.x;
                    avgY = me.y;
                    moves = 1;

                }
                Common.basicMove(rc, best);
            }
        }

        for (RobotInfo ri : nearby) {
            if (ri.type == RobotType.ZOMBIEDEN || (ri.type == RobotType.ARCHON && ri.team != team)) {

                SignalInfo si = new SignalInfo();
                si.type = SignalType.FOUND_ROBOT;
                si.targetLoc = ri.location;
                si.data = ri.type.ordinal();

                c.sendSignal(si, 10);
            }
        }t
    }

    @Override
    public Mood swing() {

        return null;
    }
}
