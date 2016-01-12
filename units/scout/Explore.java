package team018.units.scout;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.comm.SignalType;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.FieldController;
import team018.frameworks.movement.Force;
import team018.frameworks.util.Common;

import java.util.BitSet;
import java.util.HashMap;


/**
 * Created by alexhuleatt on 1/6/16.
 */
public class Explore extends Mood {

    int avgX;
    int avgY;
    int moves;


    FieldController fc;
    BitSet visited;
    Comm c;
    HashMap<Integer, MapLocation> archon_positions;
    HashMap<Integer, Integer> broadcast_cds;

    RobotInfo[] nearby;



    public Explore(RobotController rc) {
        super(rc);
        me = rc.getLocation();
        avgX=me.x;
        avgY=me.y;
        moves = 1;

        //p = new Potential(rc, en_costs, al_costs, false);
        fc = new FieldController(rc);
        fc.can_fly=true;
        Force stdForce = new Force(rc) {
            @Override
            public double enemy(MapLocation source, MapLocation t) {
                return 1000.0 / source.distanceSquaredTo(t);
            }
        };
        fc.addForce(stdForce, RobotType.values());

        visited = new BitSet();
        this.c = new Comm(rc);

        archon_positions= new HashMap<>();
        broadcast_cds=new HashMap<>();
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
            SignalInfo si;
            while ((si=c.receiveSignal())!=null) {

                if (si.type==SignalType.ARCHON_LOC) {
                    archon_positions.put(si.robotID, si.senderLoc);
                }
            }


            for (RobotInfo ri : nearby) {
                if ((!broadcast_cds.containsKey(ri.ID) || rc.getRoundNum()-broadcast_cds.get(ri.ID) > 100) && (ri.type == RobotType.ZOMBIEDEN || (ri.type == RobotType.ARCHON && ri.team != team))) {

                    si = new SignalInfo();
                    si.type = SignalType.FOUND_ROBOT;
                    si.targetLoc = ri.location;
                    si.data = ri.type.ordinal();
                    broadcast_cds.put(ri.ID, rc.getRoundNum());
                    c.sendSignal(si, 2000);
                }
            }


            double[] adj_costs = new double[8];
            MapLocation avg = new MapLocation(avgX / moves, avgY / moves);
            MapLocation t;
            for (int i = 0; i < 8; i++) {
                t = me.add(Common.directions[i]);
                double parts = rc.senseParts(t);
                if (parts > 0) {
                    si = new SignalInfo();
                    si.targetLoc=t;
                    si.type=SignalType.FOUND_PARTS;
                    si.data=(int)(parts*1000);
                    c.sendSignal(si, 2000);
                }

                adj_costs[i] += 1000.0/t.distanceSquaredTo(avg);
                if (visited.get(t.hashCode())) {
                    adj_costs[i] += 100;
                }
                for (MapLocation m : archon_positions.values()) {
                    adj_costs[i] += 700.0/t.distanceSquaredTo(m);
                }
            }



            int best_dir = fc.findDir(nearby, adj_costs);
            if (best_dir != -1) {
                MapLocation toGo = me.add(Common.directions[best_dir]);
                avgX += toGo.x;
                avgY += toGo.y;
                moves++;
                if (moves > 30) {
                    avgX = me.x;
                    avgY = me.y;
                    moves = 1;

                }
                Common.basicMove(rc, toGo);
            }
        }

    }

    @Override
    public Mood swing() {

        return null;
    }
}
