/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team018.frameworks.util;

import battlecode.common.*;


/**
 * Common shared variables and functions used across the code.
 *
 * @author alexhuleatt
 */


public class Common {

    public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST,
        Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH,
        Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

    public static int dirToInt(Direction d) {
        switch (d) {
            case NORTH:
                return 0;
            case NORTH_EAST:
                return 1;
            case EAST:
                return 2;
            case SOUTH_EAST:
                return 3;
            case SOUTH:
                return 4;
            case SOUTH_WEST:
                return 5;
            case WEST:
                return 6;
            case NORTH_WEST:
                return 7;
            default:
                return -1;
        }
    }

    public static MapLocation intToLoc(int l) {

        byte y = (byte) (l & 255);
        byte x = (byte) (l >> 8);
        return new MapLocation(x,y);
    }

    public static int locToInt(MapLocation m) {
        if (m == null) {
            return -1;
        }
        byte x = (byte)m.x;
        byte y = (byte)m.y;
        return x << 8 | y;
    }

    public static boolean isObstacle(RobotController rc, Direction dir) throws Exception {
        return isObstacle(rc, rc.getLocation().add(dir));
    }

    public static boolean isObstacle(RobotController rc, MapLocation loc) throws Exception {
        if (rc.canSenseLocation(loc)) {
            return rc.senseRubble(loc) > GameConstants.RUBBLE_OBSTRUCTION_THRESH
                    || rc.senseRobotAtLocation(loc)!= null
                    || !rc.onTheMap(loc);
        }
        return true;
    }

    public static void basicMove(RobotController rc, MapLocation m) throws Exception {
        if (m==null)return;
        Direction d = rc.getLocation().directionTo(m);
        if (rc.canMove(d) && rc.isCoreReady()) {
            rc.move(d); //  this still throws an error somehow
        }
    }
    

    public static boolean isObstacle(RobotController rc, int dir) throws Exception {
        return isObstacle(rc, directions[dir]);
    }

}
