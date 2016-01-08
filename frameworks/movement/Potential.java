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
    private final double rubbleMult;
    private double[] enCosts;
    private double[] allyCosts;
    private Random r;
    public Potential(RobotController rc, double[] enCosts, double[] allyCosts, double rubbleMult) {
        this.rc = rc;
        this.enCosts=enCosts;
        this.allyCosts=allyCosts;
        this.rubbleMult=rubbleMult;
        this.r=new Random(rc.getID());
    }

    public MapLocation findMin(double[] init_costs) {
        RobotInfo[] nearby_units = rc.senseNearbyRobots();
        double[] adj_costs = init_costs;
        MapLocation me = rc.getLocation();
        MapLocation temp;
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
        while (!rc.canMove(Common.directions[dir]) && dir < 8) dir++;
        if (dir == 8) return null;
        int mindex = 0;
        double min_cost =adj_costs[dir] + rc.senseRubble(me.add(Common.directions[dir]))*rubbleMult;
        int count = 1;
        for (int i = dir; i < 8; i++) {
            double temp_cost = adj_costs[i] + rc.senseRubble(me.add(Common.directions[i]))*rubbleMult;

            if (rc.canMove(Common.directions[i]) && temp_cost<min_cost) {
                count=1;
                mindex=i;
                min_cost = temp_cost;
            }
            else if(Math.abs(temp_cost-min_cost)<1) {
                count++;
                if (r.nextDouble()<1.0/count) {
                    mindex=i;
                    min_cost=temp_cost;
                }
            }

        }
        return me.add(Common.directions[mindex]);
    }


}
