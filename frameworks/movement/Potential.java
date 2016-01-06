package team018.frameworks.movement;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team018.frameworks.util.Common;

import java.util.HashMap;

/**
 * Created by alexhuleatt on 1/6/16.
 */
public class Potential {

    private RobotController rc;
    public HashMap<RobotType, Double> costs;
    private final double rubbleMult;

    public Potential(RobotController rc, RobotType[] types, double[] robCosts, double rubbleMult) {
        this.rc = rc;
        for (int i = 0; i < types.length; i++) {
            costs.put(types[i], robCosts[i]);
        }
        this.rubbleMult=rubbleMult;
    }

    public MapLocation findMin(double[] init_costs) {
        RobotInfo[] nearby_units = rc.senseNearbyRobots();
        int[] adj_costs = new int[8];
        MapLocation me = rc.getLocation();
        for (RobotInfo r : nearby_units) {
            for (int i = 0; i < 8; i++){
                adj_costs[i] -= costs.get(r.type)/Math.sqrt(me.distanceSquaredTo(r.location)) * ((r.team == rc.getTeam())?1:-1);
            }
        }
        int mindex = 0;
        double min_cost = Double.POSITIVE_INFINITY;
        for (int i = 0; i < 8; i++) {
            double temp_cost = adj_costs[i] * rc.senseRubble(me.add(Common.directions[i]))*rubbleMult + init_costs[i];
            if (temp_cost < min_cost) {
                mindex=i;
                min_cost = temp_cost;
            }
        }
        return me.add(Common.directions[mindex]);
    }


}
