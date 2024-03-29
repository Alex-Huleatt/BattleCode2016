package team018.frameworks.moods;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.Team;

/**
 * The Mood class is effectively a modified Turing-machine.
 * Each Mood is a state that uses the robot's environment to perform an action
 * and transfer states. 
 * @author alexhuleatt
 */
public abstract class Mood {

    public final RobotController rc;
    public MapLocation me;

    public final Team team;

    public Mood(RobotController rc) {
        this.rc = rc;
        this.team = rc.getTeam();
    }

    /**
     * Updates all the variables that the robot will most likely need. current
     * location, nearby units, etc. So that run and swing can see them without
     * having to calculate them.
     */
    public void update() {
        me = rc.getLocation();
    }

    /**
     * Transitions the robot to a different mood.
     *
     * @return The new mood, null or the same type for no swing.
     */
    public Mood swing() {
        return null;
    }

    /**
     * Does not make sense to have a default, really.
     */
    public abstract void act() throws Exception;

}
