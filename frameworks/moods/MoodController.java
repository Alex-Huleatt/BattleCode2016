/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team018.frameworks.moods;

import battlecode.common.RobotController;

/**
 *
 * @author alexhuleatt
 */
public class MoodController {
    public final RobotController rc;
    public Mood m;
    
    public MoodController(RobotController rc, Mood initialMood) {
        this.rc = rc;
        m=initialMood;
    }
    
    public final void run() {
        m.update();
        Mood swung = m.swing();
        if (swung != null && m.getClass() != swung.getClass()) {
            m = swung;
        }
        m.act();
    }
}
