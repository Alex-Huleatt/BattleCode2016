package team018.units.scout;

import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team018.frameworks.moods.Mood;

/**
 * Created by alexhuleatt on 1/4/16.
 */
public class ScoutDefault extends Mood {

    public ScoutDefault(RobotController rc) {
        super(rc);
    }

    @Override
    public void act() {

    }

    @Override
    public Mood swing() {
        //  Look for our own robots
        RobotInfo[] friends = rc.senseNearbyRobots(RobotType.SCOUT.sensorRadiusSquared, rc.getTeam());
        for (RobotInfo bot: friends)
        {
            switch (bot.type)
            {
                case TURRET:
                    return new Reporter(rc);
                default:
            }
        }
        return new Explore(rc);
    }
}
