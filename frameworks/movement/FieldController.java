package team018.frameworks.movement;

import battlecode.common.*;
import team018.frameworks.util.Common;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by alexhuleatt on 1/10/16.
 */
public class FieldController {

    public ArrayList<ArrayList<Force>> forces;
    public final RobotController rc;
    public boolean can_fly = false;
    Random r;

    public FieldController(RobotController rc) {
        this.rc = rc;
        forces = new ArrayList<>();
        for (int i = 0; i < RobotType.values().length; i++) {
            forces.add(new ArrayList<>());
        }
        r = new Random(rc.getID());
    }

    public void addForce(Force f, RobotType[] rts) {
        for (RobotType rt : rts) {
            int or = rt.ordinal();
            forces.get(rt.ordinal()).add(f);
        }
    }

    private double applyRobot(RobotInfo ri, MapLocation m) {
        double total = 0.0;
        Team me = rc.getTeam();
        for (Force f : forces.get(ri.type.ordinal())) {
            if (me == ri.team) {
                total += f.ally(ri.location, m);
            } else {
                total += f.enemy(ri.location, m);
            }
        }
        return total;
    }

    public int findDir(RobotInfo[] rarr, double[] costs) throws Exception {
        MapLocation me = rc.getLocation();
        for (int i = 0; i < 8; i++) {
            if ((!can_fly && Common.isObstacle(rc, Common.directions[i])) || !rc.canMove(Common.directions[i])) {
                costs[i] = Double.POSITIVE_INFINITY;
            }
        }

        int mindex = 0;
        int count = 1;
        boolean hereBool = (costs.length > 8);
        for (RobotInfo ri : rarr) {
            if (hereBool) {
                costs[8]+=applyRobot(ri,me);
            }
            for (int i = 0; i < 8; i++) {
                if (rc.canMove(Common.directions[i])) {
                    costs[i]+=applyRobot(ri,me.add(Common.directions[i]));

                }
            }
        }
        for (int i = 0; i < costs.length; i++) {
            if (costs[i] < costs[mindex]) {
                mindex = i;
                count = 1;
            } else if (Math.abs(costs[i]-costs[mindex]) < 1) {
                count++;
                if (r.nextDouble() < 1.0 / count) {
                    mindex = i;
                }
            }
        }
        if (costs[mindex]==Double.POSITIVE_INFINITY) return -1;
        return mindex;
    }
}
