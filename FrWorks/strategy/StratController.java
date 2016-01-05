/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team018.FrWorks.strategy;

import team018.FrWorks.jobs.Job;
import team018.FrWorks.jobs.JobController;
import battlecode.common.RobotController;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;

/**
 *
 * @author alexhuleatt
 */
public class StratController {

    private final JobController jobC;
    private final RobotController rc;

    private HashSet<Job> activeJobs;
    private HashSet<Strat> activeStrats;

    public StratController(RobotController rc, JobController jobC) {
        this.rc = rc;
        this.jobC = jobC;
    }

    public void update() {
        updateStrats();
        updateJobs();
    }

    /**
     * TODO
     */
    private void updateStrats() {
        //this list is ordered by priority.
        ArrayList<Strat> possStrats = getStrats();

    }

    /**
     * TODO Current FrWorks.jobs should depend on current strats.
     */
    private void updateJobs() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    /**
     * TODO Returns how important a job is.
     *
     * @param j
     * @return
     */
    public double job_priority(Job j) {
        return 0.0;
    }

    /**
     * Determines all possible strategies that we would realistically want to
     * do. Ordered by priority.
     *
     * @return
     */
    private ArrayList<Strat> getStrats() {
        EnumSet<Fact> trueFacts = getTrueFacts();
        ArrayList<Strat> trueStrats = new ArrayList<>();
        for (Strat s : Strat.values()) {
            if (s.reasonable(trueFacts)) {
                trueStrats.add(s);
            }
        }
//        Collections.sort(
//                trueStrats,
//                (Strat o1, Strat o2) -> Double.compare(o2.priority, o1.priority));
        return trueStrats;
    }

    private EnumSet<Fact> getTrueFacts() {
        EnumSet<Fact> facts = EnumSet.noneOf(Fact.class);
        for (Fact f : Fact.values()) {
            if (f.isTrue(rc)) {
                facts.add(f);
            }
        }
        return facts;
    }

    static enum Strat {

        ZERG(0.0) {

            @Override
            public void strategize() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ArrayList<Job> getJobs() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean reasonable(EnumSet<Fact> facts) {
                return facts.contains(Fact.SMALL_MAP) || facts.contains(Fact.EARLY_GAME);
            }
        },
        DEFEND(1.0) {

            @Override
            public void strategize() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public ArrayList<Job> getJobs() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public boolean reasonable(EnumSet<Fact> facts) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };

        final double priority;

        Strat(double priority) {
            this.priority = priority;
        }
        
        
        public abstract void strategize();
        public abstract ArrayList<Job> getJobs();
        public abstract boolean reasonable(EnumSet<Fact> facts);
    }

}
