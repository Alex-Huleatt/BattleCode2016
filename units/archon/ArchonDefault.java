package team018.units.archon;

import battlecode.common.*;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.comm.SignalType;
import team018.frameworks.moods.Mood;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by alexhuleatt on 1/4/16.
 * <p>
 * Update by Todd: Attempts to spawn a random unit based on a simple probability check.
 * Should spawn in a random direction.
 */
public class ArchonDefault extends Mood
{
    Random rand;
    Team us;
    Comm c;
    RobotType lastSpawned, toSpawn;

    int sensorRadiusSquared;
    int loc_broadcast_cd;
    int turnsSinceSpawn = 0;
    boolean ready;
    public HashMap<Integer, MapLocation> archon_positions;
    public HashMap<Integer, MapLocation> den_positions;
    RobotInfo[] hostile;

    static final Direction[] directions = Direction.values();

    //  The likelihood of spawning any unit during default behavior
    public enum SpawnRatio
    {
        SOLDIER (RobotType.SOLDIER, 30),    //  somewhat likely (30%)
        GUARD   (RobotType.GUARD,   30),    //  somewhat likely (30%)
        VIPER   (RobotType.VIPER,   30),    //  somewhat likely (30%)
        TURRET  (RobotType.TURRET,  10),    //  not very likely (10%)
        SCOUT   (RobotType.SCOUT,   0);     //  just allow initially spawned Scouts

        RobotType type;
        int odds;
        SpawnRatio(RobotType type, int odds)
        {
            this.type = type;
            this.odds = odds;
        }
    }
    static final SpawnRatio[] ratios = SpawnRatio.values();

    private static RobotType decideSpawn(int key)
    {
        for (int i = 0; i < ratios.length; i++)
        {
            if (key < ratios[i].odds)
            {
                return ratios[i].type;
            }
            else
            {
                key -= ratios[i].odds;
            }
        }
        return null;
    }

    //  returns false if broadcasted, true otherwise
    //  Other way may be intuitive, but this saves one negation later
    protected boolean broadcastLocation() throws Exception
    {
        if (loc_broadcast_cd == 0) {
            SignalInfo si = new SignalInfo();
            si.type= SignalType.ARCHON_LOC;
            c.sendSignal(si, 5000);
            loc_broadcast_cd = 15;
            //System.out.println("broadcasted");
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
                    turnsSinceSpawn = 0;
                    return false;
                }
            }
        }
        return true;
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

    public ArchonDefault(RobotController rc)
    {
        super(rc);
        rand = new Random(rc.getID());
        us = rc.getTeam();
        sensorRadiusSquared = RobotType.ARCHON.sensorRadiusSquared;
        loc_broadcast_cd=0;
        c = new Comm(rc);
        this.archon_positions=new HashMap<>();
        den_positions = new HashMap<>();

    }

    @Override
    public void update()
    {
        super.update();

        ready = rc.isCoreReady();
        hostile = rc.senseHostileRobots(me, rc.getType().sensorRadiusSquared);
    }

    @Override
    public void act() throws Exception
    {
        if (ready) {
            SignalInfo si;

            //  looping with hashmap operations, eh?
            while ((si=c.receiveSignal())!=null) {

                if (si.type==SignalType.ARCHON_LOC) {
                    archon_positions.put(si.robotID, si.senderLoc);
                }
                if (si.type==SignalType.FOUND_ROBOT && RobotType.values()[si.data]==RobotType.ZOMBIEDEN) {
                    den_positions.put(si.robotID, si.targetLoc);
                }
            }
            //  if did NOT broadcast
            if (broadcastLocation())
            {
                //Building logic
                lastSpawned = toSpawn;

                //	Determine what to spawn next
                toSpawn = decideSpawn((rand.nextInt() % 100 + 100) % 100);
                if (buildRobot(toSpawn))
                {
                    healAdjacent();
                }
            }
        }
    }

    @Override
    public Mood swing()
    {
        return null;
    }
}
