package FrWorks.movement.potential;

import FrWorks.comm.RadioController;
import FrWorks.util.Common;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by alexhuleatt on 12/13/15.
 */
public class Field {

    private RobotController rc;
    private final RadioController radC;
    public static final int OBSTACLE_INFO = -5;
    public static final int ROBOT_COST = -2;

    public Field (RobotController rc, RadioController radC) {
        this.rc = rc;
        this.radC=radC;
    }



    public void resolve(MapLocation nextMove) throws Exception{
        MapLocation me = rc.getLocation();
        HashSet<MapLocation> closed = new HashSet<MapLocation>();
        ArrayDeque<MapLocation> open = new ArrayDeque<>();
        int rad = rc.getType().sensorRadiusSquared;
        for (int i = -1 * rad; i <= rad; i++) {
            for (int j = -1 * rad; j <= rad; j++) {
                MapLocation t = new MapLocation(me.x + i, me.y + j);
                open.add(t);
            }
        }
        while (!open.isEmpty()) {
            MapLocation t = open.pop();
            if (rc.canSenseLocation(t)) {
                int old_info = readCellInfo(t);
                if (closed.contains(move(t, old_info >> 24))) {
                    continue;
                }
                int new_info = findCellInfo(t);
                if (old_info != new_info) {
                    setCellInfo(t, new_info);
                    MapLocation[] neigh = neighbors(t);
                    open.addAll(Arrays.asList(neigh));
                } else {
                    closed.add(t);
                }
            }
        }


    }

    public MapLocation move(MapLocation m, int dir) {
        switch (dir) {
            case 0:
                return new MapLocation(m.x,m.y-1);
            case 1:
                return new MapLocation(m.x+1,m.y+1);
            case 2:
                return new MapLocation(m.x+1,m.y);
            case 3:
                return new MapLocation(m.x+1,m.y+1);
            case 4:
                return new MapLocation(m.x,m.y+1);
            case 5:
                return new MapLocation(m.x-1,m.y+1);
            case 6:
                return new MapLocation(m.x-1,m.y);
            case 7:
                return new MapLocation(m.x-1,m.y-1);
            default:
                return m;
        }
    }


    public int findCellInfo(MapLocation m) throws Exception {
        if (Common.isObstacle(rc,m)) {
            return OBSTACLE_INFO;
        }

        int best=Integer.MIN_VALUE;
        int best_dir = -1;
        MapLocation[] neigh = neighbors(m);

        for (int i = 0; i < neigh.length; i++) {
            MapLocation n = neigh[i];
            int nInfo = readCellInfo(n);
            if (Common.isObstacle(rc,n)) {
                continue; // never point to obstacle.
            }
            if ((nInfo&0x0FFFFFF) > best) {
                best=nInfo&0x0FFFFFF;
                best_dir=i;
            }
        }
        if (rc.senseRobotAtLocation(m) != null) {
            if (best < ROBOT_COST) {
                best=ROBOT_COST;
                best_dir=8;
            }
        }
        return (best_dir << 24) | best;
    }

    public MapLocation[] neighbors(MapLocation m) throws Exception {
        ArrayList<MapLocation> arr = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            MapLocation n = move(m,i);
            if (!Common.isObstacle(rc, n)) {
                arr.add(n);
            }
        }
        return arr.toArray(new MapLocation[0]);
    }

    //return the cellInfo from the radio controller
    public int readCellInfo(MapLocation m) {
        return radC.getCellInfo(m);
    }


    public void setCellInfo(MapLocation m, int n) {
        radC.setCellInfo(m,n);
    }










}
