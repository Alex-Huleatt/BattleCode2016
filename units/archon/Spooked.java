package team018.units.archon;

import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.comm.SignalType;
import team018.frameworks.moods.Mood;

/**
 * Created by alexhuleatt on 1/5/16.
 */
public class Spooked extends Mood {

    RobotInfo[] hostile;
    Comm c;
    public Spooked(RobotController rc) {
        super(rc);
        c = new Comm(rc);
    }

    @Override
    public void update() {
        super.update();
        hostile = rc.senseHostileRobots(me, rc.getType().sensorRadiusSquared);
    }

    @Override
    public Mood swing() {
        if (hostile.length == 0) {
            return new ArchonDefault(rc);
        }
        return null;
    }

    @Override
    public void act() throws Exception {
        SignalInfo halp_signal = new SignalInfo();
        halp_signal.type = SignalType.HALP;
        c.sendSignal(halp_signal,2000);
        rc.setIndicatorString(0,":O");

    }
}
