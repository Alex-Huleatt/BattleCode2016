package team018.units.archon;

import battlecode.common.*;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.comm.SignalType;
import team018.frameworks.moods.Mood;

import java.util.Random;

/**
 * Created by alexhuleatt on 1/4/16.
 * <p>
 * Update by Todd: Attempts to spawn a random unit based on a simple probability check.
 * Should spawn in a random direction.
 */
public class ArchonDefault extends Mood
{
    static final Direction[] directions = Direction.values();


    Random rand;
    Team us;
    Comm c;
    RobotType lastSpawned, toSpawn;

    int sensorRadiusSquared;
    int loc_broadcast_cd;
    boolean ready;

    //  returns false if broadcasted, true otherwise
    //  Other way may be intuitive, but this saves one negation later
    protected boolean broadcastLocation() throws Exception
    {
        if (loc_broadcast_cd == 0) {
            SignalInfo si = new SignalInfo();
            si.type= SignalType.ARCHON_LOC;
            c.sendSignal(si, 2000);
            loc_broadcast_cd = 30;
            return false;
        } else {
            loc_broadcast_cd--;
            return true;
        }
    }

    //  returns false if built, true if not built
    //  Other way may be intuitive, but this saves one negation later
    protected boolean buildRobot(RobotType type) throws Exception
    {
        if (rc.hasBuildRequirements(type))
        {
            //	Build. Goes in order of direction looking for place to build
            for (Direction d : directions)
            {
                if (rc.canBuild(d, type))
                {
                    rc.build(d, type);
                    return true;
                }
            }
        }
        return false;
    }

    protected void healAdjacent() throws Exception
    {
        RobotInfo[] allies = rc.senseNearbyRobots(sensorRadiusSquared, us);
        int x, y;
        MapLocation location;
        for (RobotInfo robot : allies)
        {
            //  Don't waste time repairing full-health allies
            if (robot.health < robot.type.maxHealth)
            {
                location = robot.location;
                x = me.x - location.x;
                y = me.y - location.y;
                if (x * x + y * y <= 2 && robot.type != RobotType.ARCHON)
                {
                    rc.repair(location);
                    return;
                }
            }
        }
    }

    //	The highest-to-lowest preferences of robots to build
    static final RobotType[] robotTypes = {
            RobotType.SCOUT,
            RobotType.SOLDIER,
            RobotType.GUARD,
            RobotType.VIPER,
            RobotType.TURRET,
    };

    public ArchonDefault(RobotController rc)
    {
        super(rc);
        rand = new Random(rc.getID());
        us = rc.getTeam();
        sensorRadiusSquared = RobotType.ARCHON.sensorRadiusSquared;
        loc_broadcast_cd=0;
        c = new Comm(rc);
    }

    @Override
    public void update()
    {
        super.update();

        ready = rc.isCoreReady();
    }

    @Override
    public void act() throws Exception
    {
        if (ready && broadcastLocation())
        {

            //Building logic
            lastSpawned = toSpawn;

            //	Determine what to spawn next
            int index = rand.nextInt() % 5;
            toSpawn = robotTypes[index < 0 ? -1 * index : index];

            //  try to build
            if (buildRobot(toSpawn))
            {
                //  Didn't build, so just heal
                healAdjacent();
            }
        }
    }

    @Override
    public Mood swing()
    {
        return null;
    }
}
