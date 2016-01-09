package team018.frameworks.comm;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Signal;
import team018.frameworks.util.Common;

/**
 * Created by alexhuleatt on 1/6/16.
 */
public class Comm {

    RobotController rc;
    public Comm(RobotController rc) {
        this.rc = rc;
    }

    public void sendSignal(SignalInfo si, int radSqrd) throws Exception {
        //serialize signalinfo

        //4 bits for sig
        //4 bits for type.
        //8 bits for relative target x.
        //8 bits for relative target y.

        MapLocation me = rc.getLocation();
        MapLocation targ = si.targetLoc;
        int rel = Common.locToInt(new MapLocation(targ.x-me.x,targ.y-me.y)); //16 bits
        int typ = si.type.ordinal(); //4 bits
        int f1=0;


        f1 |= (rel << 4);
        f1 |= typ;
        f1 &= (3 << 28)|0x0FFFFFFF;
        rc.broadcastMessageSignal(f1,si.data,radSqrd);
    }

    public SignalInfo receiveSignal() {
        Signal s = rc.readSignal();
        if (s == null) return null;
        SignalInfo si = new SignalInfo();
        int[] details = s.getMessage();
        if (details != null) {
            int f1 = details[0];
            if ((f1 >> 28) == 3) {
                int typ = f1 & 0x0000000F;
                if (typ < SignalType.values().length) {
                    si.type = SignalType.values()[typ];
                }
                f1 >>= 4;
                MapLocation rel_loc = Common.intToLoc(f1);
                MapLocation send_loc = s.getLocation();
                si.targetLoc = new MapLocation(rel_loc.x+send_loc.x, rel_loc.y + send_loc.y);
            }

            si.data = details[1];
        }
        si.senderLoc=s.getLocation();
        si.senderTeam=s.getTeam();
        si.robotID = s.getRobotID();
        return si;

    }



}
