package team018.units.archon;

import battlecode.common.*;
import team018.frameworks.comm.Comm;
import team018.frameworks.comm.SignalInfo;
import team018.frameworks.comm.SignalType;
import team018.frameworks.moods.Mood;
import team018.frameworks.movement.FieldController;
import team018.frameworks.util.Common;
import team018.frameworks.util.GlobalMap;
import team018.frameworks.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
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
    RobotType toSpawn;

    int sensorRadiusSquared;
    int loc_broadcast_cd;
    int turnsSinceSpawn = 0;
    boolean ready;
    public HashMap<Integer, MapLocation> archon_positions;
    public HashMap<Integer, MapLocation> den_positions;
    public HashSet<Pair<MapLocation, Integer>> parts_positions;
    RobotInfo[] hostile;
    RobotInfo[] neutrals;
    FieldController fc;

    static final Direction[] directions = Direction.values();

    //  The likelihood of spawning any unit during default behavior
    public enum SpawnRatio
    {
        SOLDIER (RobotType.SOLDIER, 65),
        GUARD   (RobotType.GUARD,    5),
        VIPER   (RobotType.VIPER,    0),
        TURRET  (RobotType.TURRET,  20),
        SCOUT   (RobotType.SCOUT,   10);

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

    protected void receiveSignals()
    {
        SignalInfo si;

        //  looping with hashmap operations, eh?
        while ((si=c.receiveSignal())!=null) {

            if (si.type==SignalType.ARCHON_LOC) {
                archon_positions.put(si.robotID, si.senderLoc);
            }
            if (si.type==SignalType.FOUND_ROBOT && RobotType.values()[si.data]==RobotType.ZOMBIEDEN) {
                den_positions.put(si.robotID, si.targetLoc);
            }
            if (si.type==SignalType.FOUND_PARTS)
            {
                parts_positions.add(new Pair<>(si.targetLoc, si.data));
            }
        }
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
                    return true;
                }
            }
        }
        return false;
    }

    protected double[] init_costs()
    {
        double[] costs = new double[8];
        HashSet<Pair<MapLocation, Integer>> updt_parts = new HashSet<>();
        for (Pair<MapLocation, Integer> parts: parts_positions)
        {
            if (!(rc.canSenseLocation(parts.a) && rc.senseParts(parts.a)==0)) {
                updt_parts.add(parts);
            }

            for (int i = 0; i < 8; i++) {
                MapLocation to = me.add(Common.directions[i]);
                costs[i] -= (parts.b * 3000.0) / to.distanceSquaredTo(parts.a);
            }

        }
        parts_positions=updt_parts;

        for (RobotInfo neuts: neutrals)
        {

            for (int i = 0; i < 8; i++) {
                MapLocation to = me.add(Common.directions[i]);
                costs[i] -= 1000 / to.distanceSquaredTo(neuts.location);
            }

        }
        parts_positions=updt_parts;
        return costs;
    }


    //  returns true if moved or cleared rubble
    public boolean move() throws Exception
    {
        int best_dir = fc.findDir(rc.senseNearbyRobots(), init_costs());
        if (best_dir != -1) {
            if (best_dir < 8)
            {
                MapLocation dest = me.add(Common.directions[best_dir]);
                if (!Common.isObstacle(rc, best_dir)){
                    Common.basicMove(rc, dest);
                    return true;
                }
                else
                {
                    if (rc.senseRubble(dest) >= GameConstants.RUBBLE_OBSTRUCTION_THRESH)
                    {
                        rc.clearRubble(Common.directions[best_dir]);
                        return true;
                    }
                }
            }
        }
        return true;
    }

    //  returns true if healed, false otherwise
    protected boolean healAdjacent() throws Exception
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
                    return true;
                }
            }
        }
        return false;
    }


    public ArchonDefault(RobotController rc)
    {
        super(rc);

        fc = new FieldController(rc);
        fc.can_fly=true;
        rand = new Random(rc.getID());
        us = rc.getTeam();
        sensorRadiusSquared = RobotType.ARCHON.sensorRadiusSquared;
        loc_broadcast_cd=0;
        c = new Comm(rc);
        if (!GlobalMap.containsKey("archon_positions")) {
            archon_positions=new HashMap<>();
            GlobalMap.put("archon_positions", archon_positions);
        } else {
            archon_positions=GlobalMap.get("archon_positions");
        }
        if (!GlobalMap.containsKey("den_positions")) {
            den_positions=new HashMap<>();
            GlobalMap.put("den_positions", den_positions);
        } else {
            den_positions=GlobalMap.get("den_positions");
        }
        if (!GlobalMap.containsKey("parts_positions")) {
            parts_positions=new HashSet<>();
            GlobalMap.put("parts_positions", parts_positions);
        } else {
            parts_positions=GlobalMap.get("parts_positions");
        }
    }


    @Override
    public void update()
    {
        super.update();

        ready = rc.isCoreReady();
        hostile = rc.senseHostileRobots(me, rc.getType().sensorRadiusSquared);
        neutrals = rc.senseNearbyRobots(sensorRadiusSquared, Team.NEUTRAL);
        loc_broadcast_cd--;
    }

    @Override
    public void act() throws Exception
    {
        rc.setIndicatorString(0,"Default");
        if (ready) {

            receiveSignals();

            //  if did NOT broadcast
            if (broadcastLocation())
            {
                //	Determine what to spawn next
                if (toSpawn == null)
                {
                    toSpawn = decideSpawn((rand.nextInt() % 100 + 100) % 100);
                }

                if (buildRobot(toSpawn))
                {
                    toSpawn = null;
                }
                else
                {
                    if (0 < neutrals.length)
                    {
                        for (RobotInfo neutral: neutrals)
                        {
                            if (me.distanceSquaredTo(neutral.location )
                                    <= GameConstants.ARCHON_ACTIVATION_RANGE)
                            {
                                rc.activate(neutral.location);
                                break;
                            }
                        }
                    }
                    else if (!move())
                    {
                        healAdjacent();
                    }
                }
            }
        }
    }

    @Override
    public Mood swing()
    {

        if (rc.senseHostileRobots(me, 25).length > 0) {
            return new Spooked(rc);
        }
        return null;
    }
}
