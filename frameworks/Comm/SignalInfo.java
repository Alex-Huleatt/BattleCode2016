package team018.frameworks.comm;

import battlecode.common.MapLocation;

/**
 * Created by alexhuleatt on 1/6/16.
 */
public class SignalInfo {

    public MapLocation targetLoc = new MapLocation(-1,-1);
    public SignalType type = SignalType.DEFAULT;
    public short priority=0;

    public SignalInfo() { }

}
