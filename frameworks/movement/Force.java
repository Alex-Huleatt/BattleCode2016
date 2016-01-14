package team018.frameworks.movement;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * Created by alexhuleatt on 1/10/16.
 */
public abstract class Force {

    public final RobotController rc;

    public Force(RobotController rc) {
        this.rc = rc;
    }

    public double ally(MapLocation source, MapLocation t) {
        return 0.0;
    }

    public double enemy(MapLocation source, MapLocation t) { return 0.0; }

    public double misc(MapLocation t) { return 0.0; }
}
