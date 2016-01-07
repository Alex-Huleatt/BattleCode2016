package team018;

import battlecode.common.RobotController;
import team018.units.Unit;
import team018.units.archon.ArchonDefault;
<<<<<<< HEAD
import team018.units.guard.Standby;
import team018.units.scout.Explore;
=======
import team018.units.shared.Standby;
import team018.units.scout.ScoutDefault;
import team018.units.turret.SoloTurret;
>>>>>>> 06628e69813f70be3c6345993aac9c7e210220a2


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
            case VIPER:
            {
                new Unit(rc, new Standby(rc)).run();
                return;
            }
            case SCOUT:
            {
                new Unit(rc, new ScoutDefault(rc)).run();
                return;
            }
            case TURRET:
            {
                new Unit(rc, new SoloTurret(rc)).run();
                return;
            }
        }
    }
}
