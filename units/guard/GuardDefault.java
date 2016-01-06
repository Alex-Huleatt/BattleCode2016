package team018.units.guard;

import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import team018.frameworks.moods.Mood;

/**
 * Created by alexhuleatt on 1/4/16.
 */
public class GuardDefault extends Mood {
    public GuardDefault(RobotController rc) {
        super(rc);
    }

    @Override
    public void act() throws Exception {
    	
    	//	If it can attack, try!
    	if (rc.isCoreReady())
    	{
        	RobotInfo[] robots = rc.senseHostileRobots(rc.getLocation(), rc.getType().attackRadiusSquared);
        	for (RobotInfo i: robots)
        	{
        		if (rc.canAttackLocation(i.location))
        		{
        			rc.attackLocation(i.location);
        			break;
        		}
        	}
    	}
    }
}
