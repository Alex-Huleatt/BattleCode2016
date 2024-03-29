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

        byte y = (byte) (l & 0xFF);
        byte x = (byte) (l >> 8);
        return new MapLocation(x, y);
    }

    public static int locToInt(MapLocation m) {
        if (m == null) {
            return -1;
        }
        byte x = (byte) m.x;
        byte y = (byte) m.y;

        return ((x&0xFF)<<8)|(y&0xFF);
    }

    public static boolean isObstacle(RobotController rc, Direction dir) throws Exception {
        return isObstacle(rc, rc.getLocation().add(dir));
    }

    public static boolean isObstacle(RobotController rc, MapLocation loc) throws Exception {
        if (rc.canSenseLocation(loc)) {
            return rc.senseRubble(loc) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH
                    || rc.senseRobotAtLocation(loc) != null
                    || !rc.onTheMap(loc);
        }
        return true;
    }

    public static void basicMove(RobotController rc, MapLocation m) throws Exception {
        if (m == null) return;
        Direction d = rc.getLocation().directionTo(m);
        // canMove() sometimes returns true, even when the destination location can't be moved to. ¯\_(ツ)_/¯
        if (rc.isCoreReady() && rc.canMove(d)) {
            try {
                rc.move(d);
            } catch (Exception e) {
                System.out.println("plz fix canMove devs");
            }
        }
    }


    public static boolean isObstacle(RobotController rc, int dir) throws Exception {
        return isObstacle(rc, directions[dir]);
    }


    public static int getThreat(RobotInfo[] bots) throws Exception {
        int total = 0;
        for (RobotInfo r : bots) {
            total += getThreatLevel(r);
        }
        return total;
    }


    public static int getThreatLevel(RobotInfo info) {
        switch (info.type) {
            case SOLDIER:
                return 2;
            case GUARD:
                return 2;
            case BIGZOMBIE:
                return 5;
            case FASTZOMBIE:
                return 1;
            case RANGEDZOMBIE:
                return 3;
            case TURRET:
                return 4;
            case VIPER:
                return 4;
            case ARCHON:
                return -2;
            default:
                return 0;
        }
    }

    public static int getAttackPriority(RobotInfo info)
    {
        switch (info.type)
        {
            case SOLDIER:
                return 1;
            case GUARD:
                return 1;
            case BIGZOMBIE:
                return 5;
            case FASTZOMBIE:
                return 1;
            case RANGEDZOMBIE:
                return 3;
            case ZOMBIEDEN:
                return 0;
            case TURRET:
                return 3;
            case VIPER:
                return 3;
            case ARCHON:
                return 5;
            default:
                return 0;
        }
    }

}
