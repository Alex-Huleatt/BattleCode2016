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
import team018.frameworks.util.Common;

/**
 * Created by alexhuleatt on 1/5/16.
 */
public class Spooked extends ArchonDefault {

    RobotInfo[] hostile;
    Comm c;
    FieldController fc;
    int swing_cd;

    public Spooked(RobotController rc) {
        super(rc);
        c = new Comm(rc);
        fc = new FieldController(rc);
        Force stdForce = new Force(rc) {
            @Override
            public double enemy(MapLocation source, MapLocation t) {
                return 3000.0 / source.distanceSquaredTo(t);
            }
        };
        fc.addForce(stdForce, RobotType.values());
        swing_cd = 30;
    }

    @Override
    public void update() {
        super.update();
        hostile = rc.senseHostileRobots(me, rc.getType().sensorRadiusSquared);
        swing_cd--;
    }

    @Override
    public Mood swing() {
        if (swing_cd > 0) return null;
        if (hostile.length == 0) {
            return new ArchonDefault(rc);
        }
        return null;
    }

    @Override
    public void act() throws Exception {
        SignalInfo halp_signal = new SignalInfo();
        halp_signal.type = SignalType.HALP;
        rc.setIndicatorString(0,":O");
        int toGo = fc.findDir(rc.senseNearbyRobots(), new double[8]);

        if (toGo != -1 && rc.canMove(Common.directions[toGo])) {
            Common.basicMove(rc,me.add(Common.directions[toGo]));
            rc.setIndicatorString(1,""+toGo);
        } else {
            c.sendSignal(halp_signal,4000);
            if (toGo!=-1&&rc.senseRubble(me.add(Common.directions[toGo]))>0)rc.clearRubble(Common.directions[toGo]);
        }



    }
}
