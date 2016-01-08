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
    RobotType[] robs = new RobotType[]{RobotType.SOLDIER,
            RobotType.GUARD,
            RobotType.STANDARDZOMBIE,
            RobotType.ARCHON,
            RobotType.VIPER,RobotType.ZOMBIEDEN};
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
    }

    @Override
    public void act() throws Exception {
        double[] adj_costs = new double[8];
        MapLocation avg=new MapLocation(avgX/moves,avgY/moves);
        for (int i = 0; i < 8; i++) {
            adj_costs[i] = -2*me.add(Common.directions[i]).distanceSquaredTo(avg);
        }
        //System.out.println(Arrays.toString(adj_costs));
        MapLocation best = p.findMin(adj_costs);
        avgX+=best.x;
        avgY+=best.y;
        moves++;
        if (moves > 30) {
            avgX = me.x;
            avgY = me.y;
            moves = 1;

        }
        Common.basicMove(rc,best);
        rc.setIndicatorString(0,best.toString());

    }

    @Override
    public Mood swing() {

        return null;
    }
}
