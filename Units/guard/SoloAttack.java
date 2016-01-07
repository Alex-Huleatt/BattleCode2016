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
 *
 * Update by Todd
 */
public class SoloAttack extends Mood
{

    int attackRangeSquared, sensorRangeSquared;
    MovementController mc;
    RobotInfo[] hostile;
    public SoloAttack(RobotController rc)
    {
        super(rc);
        attackRangeSquared = RobotType.GUARD.attackRadiusSquared;
        sensorRangeSquared = RobotType.GUARD.sensorRadiusSquared;
        mc = new MovementController(rc);
    }

    @Override
    public void update() {
        super.update();
        hostile=rc.senseHostileRobots(me, sensorRangeSquared);
    }

    @Override
    public void act() throws Exception
    {

        // If it can attack, try!
        if (rc.isCoreReady())
        {


            rc.setIndicatorString(0, "Robots: " + hostile.length);

            RobotInfo closest = null;
            MapLocation closestLocation = null;
            MapLocation checkLocation;
            int distance = Integer.MAX_VALUE, checkDistance, x, y;
            for (int i = 0; i < hostile.length; i++)
            {
                checkLocation = hostile[i].location;
                System.out.println(me + " " + checkLocation);
                x = me.x - checkLocation.x;
                y = me.y - checkLocation.y;
                checkDistance = x * x + y * y;
                if (checkDistance < distance)
                {
                    closest = hostile[i];
                    closestLocation = checkLocation;
                    distance = checkDistance;
                }
            }
            // It found nothing. Go back to rally point
            if (closest == null)
            {

            }
            //
            else
            {
                rc.setIndicatorString(1, "Targetting " + closest.location.toString());
                if (rc.canAttackLocation(closestLocation))
                {
                    rc.attackLocation(closestLocation);
                }
                else
                {
                    Direction direction = me.directionTo(closestLocation);
                    if (rc.canMove(direction))
                    {
                        rc.move(direction);
                    }
                    else
                    {
                        MapLocation destination = me.add(direction);
                        if (GameConstants.RUBBLE_OBSTRUCTION_THRESH < rc.senseRubble(destination))
                        {
                            rc.clearRubble(direction);
                        }
                    }
                }
            }
        }
    }


    @Override
    public Mood swing()
    {
        if (0 == hostile.length)
        {
            return new Standby(rc);
        }
        return null;
    }
}
