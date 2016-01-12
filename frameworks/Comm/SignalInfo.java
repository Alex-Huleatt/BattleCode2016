package team018.frameworks.comm;

import battlecode.common.MapLocation;
import battlecode.common.Team;

/**
 * Created by alexhuleatt on 1/6/16.
 */
public class SignalInfo {

    public MapLocation targetLoc = new MapLocation(-1,-1);
    public SignalType type = SignalType.DEFAULT;
    public MapLocation senderLoc = new MapLocation(-1,-1);
    public Team senderTeam = Team.A;
    public int robotID = -1;
    public boolean basic=false;

    public int data = -1;

    public SignalInfo() { }

}
