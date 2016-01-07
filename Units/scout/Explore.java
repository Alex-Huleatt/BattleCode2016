package team018.units.scout;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.Potential;
import team018.frameworks.util.Common;

/**
 * Created by alexhuleatt on 1/6/16.
 */
public class Explore extends Mood {

    int avgX;
    int avgY;
    int moves;
    Potential p;
    public Explore(RobotController rc) {
        super(rc);
        me = rc.getLocation();
        avgX=me.x;
        avgY=me.y;
        moves = 0;
        p = new Potential(rc, new RobotType[]{RobotType.SOLDIER}, new double[]{1.0}, new double[]{0.0}, 0.0);
    }

    @Override
    public void act() throws Exception {
        double[] adj_costs = new double[8];
        MapLocation avg=new MapLocation(avgX,avgY);
        for (int i = 0; i < 8; i++) {
            adj_costs[i] = 1.0/Math.sqrt(me.add(Common.directions[i]).distanceSquaredTo(avg));
        }
        MapLocation best = p.findMin(adj_costs);
        avgX+=best.x;
        avgY+=best.y;
        rc.move(me.directionTo(best));
    }

    @Override
    public Mood swing() {

        return null;
    }
}
