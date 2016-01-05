package team018.units;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import team018.frameworks.moods.MoodController;

/**
 * Created by alexhuleatt on 1/4/16.
 */
public class Unit {

    public RobotController rc;
    public MoodController mc;

    public Unit(RobotController rc) {
        this.rc = rc;
    }


    public final void step() {
        while (true) {
            mc.run();
            Clock.yield();
        }
    }
}
