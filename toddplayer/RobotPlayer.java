package toddplayer;

import battlecode.common.*;

import java.util.Random;

import FrWorks.movement.MovementController;


public class RobotPlayer {
	public static void run(RobotController rc)
	{try{	
		Random rand = new Random(rc.getID());
		Team us = rc.getTeam();
		Team them = us.opponent();
		
		MovementController mc = new MovementController(rc);

		//	Test movement: go to a target location once.
		MapLocation loc = rc.getLocation();
		loc.add(10, 5);
		
		while (true)
		{
			//	do the movement
			mc.bug(loc);
			Clock.yield();
		}
		}
		
	catch(Exception e)
	{
		e.printStackTrace();
	}}
}
