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
    public HashMap<RobotType, Double> en_map;
    public HashMap<RobotType, Double> ally_map;
    private final double rubbleMult;

    public Potential(RobotController rc, RobotType[] types, double[] enCosts, double[] allyCosts, double rubbleMult) {
        this.rc = rc;
        for (int i = 0; i < types.length; i++) {
            en_map.put(types[i], enCosts[i]);
            ally_map.put(types[i], allyCosts[i]);
        }
        this.rubbleMult=rubbleMult;
    }

    public MapLocation findMin(double[] init_costs) {
        RobotInfo[] nearby_units = rc.senseNearbyRobots();
        int[] adj_costs = new int[8];
        MapLocation me = rc.getLocation();
        for (RobotInfo r : nearby_units) {
            for (int i = 0; i < 8; i++){
                if (r.team != rc.getTeam()) {
                    if (en_map.containsKey(r.type)) {
                        adj_costs[i] += en_map.get(r.type);
                    }
                } else {
                    if (ally_map.containsKey(r.type)) {
                        adj_costs[i]-=ally_map.get(r.type);
                    }
                }
            }
        }
        int mindex = 0;
        double min_cost = Double.POSITIVE_INFINITY;
        for (int i = 0; i < 8; i++) {
            double temp_cost = adj_costs[i] * rc.senseRubble(me.add(Common.directions[i]))*rubbleMult + init_costs[i];
            if (temp_cost < min_cost && rc.canMove(Common.directions[i])) {
                mindex=i;
                min_cost = temp_cost;
            }
        }
        return me.add(Common.directions[mindex]);
    }


}
