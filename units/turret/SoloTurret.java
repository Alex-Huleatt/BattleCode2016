package team018.units.turret;

import battlecode.common.*;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.MovementController;

/**
 * Created by alexhuleatt on 1/4/16.
 *
 * Update by Todd - Guards and Soldiers should both be able to use this
 */
public class SoloTurret extends Mood
{

    int attackRangeSquared, // unused
        sensorRangeSquared,
        attackRangeMin;
    MovementController mc;
    RobotInfo[] hostile;
    public SoloTurret(RobotController rc)
    {
        super(rc);
        RobotType type = rc.getType();
        attackRangeSquared = RobotType.TURRET.attackRadiusSquared;
        sensorRangeSquared = RobotType.TURRET.sensorRadiusSquared;
        attackRangeMin  = GameConstants.TURRET_MINIMUM_RANGE;
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
        if (rc.isCoreReady() && rc.isWeaponReady())
        {


            rc.setIndicatorString(0, "Robots: " + hostile.length);

            RobotInfo closest = null;
            MapLocation closestLocation = null, checkLocation;
            int distance = Integer.MAX_VALUE, checkDistance, x, y;
            for (int i = 0; i < hostile.length; i++)
            {
                checkLocation = hostile[i].location;
                System.out.println(me + " " + checkLocation);
                x = me.x - checkLocation.x;
                y = me.y - checkLocation.y;
                checkDistance = x * x + y * y;
                //  Turrets have a minimum attacking range, so don't bother
                //  with any enemies that might be closer
                if (checkDistance < distance && attackRangeMin <= distance)
                {
                    closest = hostile[i];
                    closestLocation = checkLocation;
                    distance = checkDistance;
                }
            }
            if (closest != null)
            {
                rc.setIndicatorString(1, "Targetting " + closest.location.toString());
                if (rc.canAttackLocation(closestLocation))
                {
                    rc.attackLocation(closestLocation);
                }
            }
            //  Turrets can't move, so there's nothing else to do in SoloTurret mode.
        }
    }


    @Override
    public Mood swing()
    {
        //  In the future this should allow for GroupAttack mood
        return null;
    }
}
