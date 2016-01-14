package team018.units.turret;

import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.comm.SignalType;
import team018.frameworks.moods.Mood;

/**
 * Created by Dover on 1/13/2016.
 */
public class TeamTurret extends Mood
{
    Comm c;
    MapLocation target;
    public TeamTurret (RobotController rc, MapLocation target)
    {
        super(rc);
        c = new Comm(rc);
        this.target = target;
    }

    @Override
    public void update()
    {
        super.update();

        //  listen for more
        if (target == null)
        {
            SignalInfo si;

            while ((si = c.receiveSignal()) != null)
            {

                //  6 = 2 attacking rounds for a turret
                if (si.type == SignalType.ATTACK)// && si.data < 6)
                {
                    target = si.targetLoc;
                    return;
                }
            }
        }
    }

    @Override
    public void act() throws Exception
    {
        rc.setIndicatorString(0, "Team");

        if (rc.isCoreReady() && rc.isWeaponReady() && rc.canAttackLocation(target)
                && GameConstants.TURRET_MINIMUM_RANGE <= me.distanceSquaredTo(target))
        {
            rc.attackLocation(target);
            target = null;
        }
    }

    @Override
    public Mood swing()
    {
        if (target == null)
        {
            return new SoloTurret(rc);
        }
        return null;
    }
}
