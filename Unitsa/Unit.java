package team018.units;

import battlecode.common.Clock;
import battlecode.common.RobotController;
import team018.frameworks.moods.Mood;
import team018.frameworks.moods.MoodController;

/**
 * Created by alexhuleatt on 1/4/16.
 */
public class Unit {

    public RobotController rc;
    public MoodController mc;

    public Unit(RobotController rc, Mood init) {
        this.rc = rc;
        this.mc = new MoodController(rc,init);
    }


    public final void run() {
        while (true) {
            mc.run();
            Clock.yield();
        }
    }
}
