/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FrWorks.movement;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.util.Arrays;
import java.util.HashSet;

import FrWorks.movement.pathplanning.AStar;
import FrWorks.util.Common;
import FrWorks.util.Geom;

/**
 * A FrWorks.movement handling class designed to have a pretty API.
 * 
 * Couple notes:
 * Modulo 8 is equivalent to bit-wise AND with 7 (% 8 --> & 7), 
 * except that it actually returns the correct value for negative numbers.
 * 
 * Directions are mapped to the integers mod 8, and then back again.
 * 
 * We have probabilistic cycle avoidance on the bugging.
 * Seems to work pretty okay.
 * 
 * @author alexhuleatt
 */
public class MovementController {

    public final RobotController rc;
    private AStar astr;
    private HashSet<MapLocation> obs;

    private MapLocation start;
    private MapLocation end;
    private boolean reverse;
    private int dir;
    private boolean bug;
    private int closest;

    private MapLocation cycle_loc;
    private boolean cycle_check;
    private double cycle_thresh;
    private static final double init_cycle_thresh = .50;
    private static final double min_cycle_thresh = .005;
    private static final double cycle_mult = .95;
    
    private MapLocation me;

    public MovementController(RobotController rc) {
        this.rc = rc;
    }

    public void logObstacle(MapLocation ob) {
        if (obs == null) {
            obs = new HashSet<>();
        }
        obs.add(ob);
    }

    public void logObstacles(MapLocation[] ob_arr) {
        getObs().addAll(Arrays.asList(ob_arr));
    }

    public HashSet<MapLocation> getObs() {
        if (obs == null) {
            obs = new HashSet<MapLocation>();
        }
        return obs;
    }

    /**
     * This will likely take a while. This is dependent on the distance between
     * start and end, and the topology of the map.
     *
     * @param start
     * @param end
     * @return
     */
    public MapLocation[] getPath(MapLocation start, MapLocation end) {
        if (astr == null) {
            astr = new AStar(getObs());
        }
        return astr.pathfind(start, end);
    }
    
    /**
     * This function is a FrWorks.movement FrWorks.strategy that is effectively bugging.
     * It was designed to be highly constrained, it only utilizes the 8 cells
     * directly neighboring the unit. 
     * As far as I can tell it's guaranteed. There is built in probabilistic 
     * cycle avoidance which *should* prevent any infinite cycles,
     * although high unit density does provide a challenge.
     * I do not think there is a good way to deal with that without more 
     * information.
     * @param goal
     * @throws Exception 
     */
    public void bug(MapLocation goal) throws Exception {
        me = rc.getLocation();

        if (goal == null) {
            return;
        }

        if (start == null || end == null || !goal.equals(end)) {
            start = rc.getLocation();
            end = goal;
            bug = false;
            closest = Integer.MAX_VALUE;
            cycle_check = false;
            cycle_loc = null;
            cycle_thresh = init_cycle_thresh;
        }

        int disToGoal = me.distanceSquaredTo(goal);
        Direction dirToGoal = me.directionTo(goal);

        if (disToGoal == 0) {
            return;
        }

        if (Geom.locOnLine(start, end, me)) {
            if (!isObs(me.add(dirToGoal))) {
                if (bug) {
                    if (disToGoal < closest) {
                        stopBugging(dirToGoal);
                        return;
                    }
                } else {
                    move(dirToGoal);
                }
            }
        }

        if (Math.random() < cycle_thresh) {
            cycle_check = false;
            cycle_loc = me;
        } else {
            cycle_thresh = Math.max(cycle_thresh * cycle_mult, min_cycle_thresh);
        }

        if (bug && cycle_loc != null && me.distanceSquaredTo(cycle_loc) == 0) {
            if (cycle_check) {
                start = null;
                bug(goal);
                return;
            } else {
                cycle_check = true;
            }
        }

        if (!bug) {
            initBug();
            return;
        }

        MapLocation nextMove = trace();
        if (nextMove != null) {
            move(me.directionTo(nextMove));
        } else {
            dir = (dir + 4) & 7;
            nextMove = trace();
            if (nextMove != null) {
                move(me.directionTo(nextMove));
            } else {
                start = null;
                bug(goal);
            }
        }
    }

    private MapLocation trace() throws Exception {
        final int sd = (reverse) ? 1 : -1;
        final int side_dir = (dir+2*sd)&7;
        Direction[] dirs = Common.directions;
        for (int i = 0; i <= 4; ++i) {
            int d = ((side_dir-i*sd)) & 7;
            if (!isObs(me.add(dirs[d])) && isObs(me.add(dirs[(d+sd)&7]))) {
                return me.add(dirs[d]);
            }
        }
        return null;
    }

    private boolean initReverse(MapLocation goal) {
        int offset = Math.min(8 - dir, dir);
        int dir_to_obs = Common.dirToInt(me.directionTo(goal));
        return ((dir_to_obs + offset) % 8) < 4;
    }

    public void move(Direction dir) throws Exception {
        if (rc.isCoreReady() && rc.canMove(dir)) {
            rc.move(dir);
            this.dir = Common.dirToInt(dir);
        } else {
            //System.out.println("rc failed to move in dir");
        }
    }

    public void move(int dir) throws Exception {
        move(Common.directions[dir]);
    }

    public boolean simpleMove(MapLocation goal) throws Exception {
        Direction d = me.directionTo(goal);
        int d_2_g = Common.dirToInt(d);
        MapLocation next;
        int td;
        for (int i = 0; i <= 4; i++) {
            td = (d_2_g + i) & 7;
            next = me.add(Common.directions[td]);
            if (!Common.isObstacle(rc, next)) {
                move(td);
                return true;
            }
            td = (d_2_g - i) & 7;
            next = me.add(Common.directions[td]);
            if (!Common.isObstacle(rc, next)) {
                move(td);
                return true;
            }
        }
        return false;
    }

    private void initBug() throws Exception {
        bug = true;
        simpleMove(end);
        reverse = initReverse(end);
        closest = me.distanceSquaredTo(end);
        cycle_loc = me;
        cycle_check = false;
        cycle_thresh = init_cycle_thresh;
    }

    private void stopBugging(Direction dirToGoal) throws Exception {
        bug = false;
        dir = Common.dirToInt(dirToGoal);
        move(dirToGoal);
    }

    public boolean isObs(MapLocation m) throws Exception {
        boolean pathable = rc.isPathable(rc.getType(), m);
        boolean hasUnit = rc.senseRobotAtLocation(m) != null;
        return !(pathable || (hasUnit && Math.random() > .9));
    }
}
