package team018.units.guard;

import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.MovementController;

/**
 * Created by alexhuleatt on 1/4/16.
 */
public class GuardDefault extends Mood
{
    MapLocation myLocation;
    int attackRangeSquared, sensorRangeSquared;
    MovementController mc;

    public GuardDefault(RobotController rc)
    {
        super(rc);
        attackRangeSquared = RobotType.GUARD.attackRadiusSquared;
        sensorRangeSquared = RobotType.GUARD.sensorRadiusSquared;
        mc = new MovementController(rc);
    }

    public void update()
    {
        myLocation = rc.getLocation();
    }

    @Override
    public void act() throws Exception
    {

        // If it can attack, try!
        if (rc.isCoreReady())
        {
            RobotInfo[] robots = rc.senseHostileRobots(myLocation, sensorRangeSquared);
            rc.setIndicatorString(0, "Robots: " + robots.length);

            RobotInfo closest = null;
            MapLocation closestLocation = null, checkLocation;
            int distance = Integer.MAX_VALUE, checkDistance, x, y;
            for (int i = 0; i < robots.length; i++)
            {
                checkLocation = robots[i].location;
                x = myLocation.x - checkLocation.x;
                y = myLocation.y - checkLocation.y;
                checkDistance = x * x + y * y;
                if (checkDistance < distance)
                {
                    closest = robots[i];
                    closestLocation = checkLocation;
                    distance = checkDistance;
                }
            }
            // Make sure it found something
            if (closest != null)
            {
                rc.setIndicatorString(1, "Targetting " + closest.location.toString());
                if (rc.canAttackLocation(closestLocation))
                {
                    rc.attackLocation(closestLocation);
                }
                else
                {
                    Direction direction = myLocation.directionTo(closestLocation);
                    if (rc.canMove(direction))
                    {
                        rc.move(direction);
                    }
                    else
                    {
                        MapLocation destination = myLocation.add(direction);
                        if (GameConstants.RUBBLE_OBSTRUCTION_THRESH < rc.senseRubble(destination))
                        {
                            rc.clearRubble(direction);
                        }
                    }
                }
            }
        }
    }
}
