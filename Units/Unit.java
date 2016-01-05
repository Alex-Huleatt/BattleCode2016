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


    public final void run() throws Exception {
        while (true) {
            mc.run();
            //should not have real behavior outside moodcontroller, can put debug info here.
            Clock.yield();
        }
    }
}
