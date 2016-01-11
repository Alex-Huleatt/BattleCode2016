package team018.frameworks.movement;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import team018.frameworks.util.Common;

import java.util.Random;

/**
 * Created by alexhuleatt on 1/6/16.
 */
public class Potential {

    private RobotController rc;
    private final boolean rubble_bool;
    private double[] enCosts;
    private double[] allyCosts;
    private Random r;

    public Potential(RobotController rc, double[] enCosts, double[] allyCosts, boolean rubble_bool) {
        this.rc = rc;
        this.enCosts=enCosts;
        this.allyCosts=allyCosts;
        this.rubble_bool=rubble_bool;
        this.r=new Random(rc.getID());
    }

    public double getForce(int x, int y) {


        return 0.0;
    }

    public MapLocation findMin(double[] init_costs) throws Exception {
        RobotInfo[] nearby_units = rc.senseNearbyRobots();
        double[] adj_costs = init_costs;
        MapLocation me = rc.getLocation();
        MapLocation temp;
        if (!rc.isCoreReady()) return null;
        for (RobotInfo r : nearby_units) {
            temp = r.location;
            for (int i = 0; i < 8; i++){
                int disSqr =  temp.distanceSquaredTo(me.add(Common.directions[i]));
                if (r.team != rc.getTeam()) {
                    adj_costs[i] += enCosts[r.type.ordinal()]/disSqr;
                } else {
                    adj_costs[i] -= allyCosts[r.type.ordinal()]/disSqr;
                }
            }
        }
        int dir = 0;
        while (dir < 8 &&!rc.canMove(Common.directions[dir])) dir++;
        if (dir == 8) return null;
        int mindex = 0;
        double min_cost =adj_costs[dir] +  ((rubble_bool && Common.isObstacle(rc, dir))?Double.POSITIVE_INFINITY:0);
        int count = 1;
        for (int i = dir; i < 8; i++) {
            if (!(rubble_bool && Common.isObstacle(rc, i))) {
                double temp_cost = adj_costs[i];

                if (rc.canMove(Common.directions[i]) && temp_cost < min_cost) {
                    count = 1;
                    mindex = i;
                    min_cost = temp_cost;
                } else if (Math.abs(temp_cost - min_cost) < 1) {
                    count++;
                    if (r.nextDouble() < 1.0 / count) {
                        mindex = i;
                        min_cost = temp_cost;
                    }
                }
            } else {
                adj_costs[i] = Double.POSITIVE_INFINITY;
            }

        }

        return me.add(Common.directions[mindex]);
    }


}
