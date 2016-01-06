package team018.frameworks.Comm;

import battlecode.common.MapLocation;

/**
 * Created by alexhuleatt on 1/6/16.
 */
public class SignalInfo {

    public MapLocation senderLoc;
    public MapLocation targetLoc;
    public SignalType type;
    public int senderID;
    public int targetID;
    public int priority=0;

    public SignalInfo() { }

}
