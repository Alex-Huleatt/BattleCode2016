package team018.units.turret;

import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
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
    final static int TIMEOUT = 3;
    int cd = TIMEOUT; //  timeout of the current location
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
        //  Timeout our info
        if (--cd < 0)
        {
            target = null;
        }

        if (target == null)
        {
            SignalInfo si, newest = null;

            while ((si = c.receiveSignal()) != null)
            {

                if (si.type == SignalType.ATTACK
                        && rc.getRoundNum() - si.data < TIMEOUT
                        //  can actually attack?
                        && me.distanceSquaredTo(si.targetLoc) <= RobotType.TURRET.attackRadiusSquared
                        && (newest == null || newest.data < si.data)
                        )
                {
                    target = si.targetLoc;
                    cd = TIMEOUT;
                    newest = si;
                }
            }
        }
    }

    @Override
    public void act() throws Exception
    {
        rc.setIndicatorString(0, "Team");
        rc.setIndicatorString(1, target.toString());
        rc.setIndicatorString(2, cd + "");

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
