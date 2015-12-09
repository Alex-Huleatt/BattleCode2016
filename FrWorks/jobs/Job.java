/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FrWorks.jobs;

import battlecode.common.MapLocation;

/**
 *
 * @author alexhuleatt
 */
public class Job {

    public JobType jt;
    public MapLocation location;
    public double priority;
    public int maxRadius;
    
    public static final int NUM_INTS = 4;

    public Job() {
    }

    public Job(int[] ints) {

        //init from serialized
    }

    public int[] serialize() {

        return null;
    }

}
