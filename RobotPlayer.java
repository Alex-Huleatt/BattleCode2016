package team018;

import battlecode.common.RobotController;
import team018.units.Unit;
import team018.units.archon.ArchonDefault;
import team018.units.guard.SoloAttack;
import team018.units.guard.Standby;
import team018.units.scout.ScoutDefault;


public class RobotPlayer
{

    /**
     * If a Unit throws an exception we catch it and re-enter
     *
     * @param rc
     */
    public static void run(RobotController rc)
    {
        while (true)
        {
            try
            {
                typeSwitch(rc);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void typeSwitch(RobotController rc) throws Exception
    {
        switch (rc.getType())
        {
            case ARCHON:
            {
                new Unit(rc, new ArchonDefault(rc)).run();
                return;
            }
            case GUARD:
            case SOLDIER:
            {
                new Unit(rc, new Standby(rc)).run();
                return;
            }
            case SCOUT:
            {
                new Unit(rc, new ScoutDefault(rc)).run();
                return;
            }
            default:
            {
                return;
            }
        }
    }
}
