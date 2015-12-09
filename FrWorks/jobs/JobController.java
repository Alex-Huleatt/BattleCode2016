/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FrWorks.jobs;

import FrWorks.comm.RadioController;
import battlecode.common.RobotController;

/**
 *
 * @author alexhuleatt
 */
public class JobController {
    
    private final RadioController radC;
    private final RobotController rc;
    
    public JobController(RobotController rc, RadioController radC) {
        this.radC = radC;
        this.rc = rc;
    }
    
    public Job findJob() {
        int numJobs = radC.queueLen("FrWorks/jobs")/Job.NUM_INTS;
        Job bestJob = null;
        double bestJob_val = 0.0;
        for (int i = 0; i < numJobs; i++) {
            Job temp = readJob(i);
            double temp_val = job_heuristic(temp);
            if (bestJob == null || temp_val > bestJob_val) {
                bestJob=temp;
                bestJob_val= temp_val;
            }
        }
        
        return bestJob;
               
    }
    
    private double job_heuristic(Job j) {
        
        return 0.0;
    }
    
    
    private Job readJob(int n) {
        int[] serial = new int[Job.NUM_INTS];
        for (int i = 0; i < serial.length; i++) {
            serial[i] = radC.getInt("FrWorks/jobs", Job.NUM_INTS * n + i);
        }
        return new Job(serial);
    }
    
}
