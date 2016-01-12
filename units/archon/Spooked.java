package team018.units.archon;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.comm.SignalType;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.FieldController;
import team018.frameworks.movement.Force;

/**
 * Created by alexhuleatt on 1/5/16.
 */
public class Spooked extends Mood {

    RobotInfo[] hostile;
    Comm c;
    FieldController fc;

    public Spooked(RobotController rc) {
        super(rc);
        c = new Comm(rc);
        fc = new FieldController(rc);
        Force stdForce = new Force(rc) {
            @Override
            public double enemy(MapLocation source, MapLocation t) {
                return 1000.0 / source.distanceSquaredTo(t);
            }
        };
        fc.addForce(stdForce, RobotType.values());
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
