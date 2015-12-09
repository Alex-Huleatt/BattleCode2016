/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import battlecode.common.Clock;
import battlecode.common.GameConstants;

/**
 *
 * @author alexhuleatt
 */
public class Timer {

    public int start;
    public int start_round;
    public int elaps;
    public boolean stopped;

    public Timer() {
        elaps = 0;
    }

    public void start() {
        stopped = false;
        start = Clock.getBytecodesLeft();
        //start_round=Clock.getRoundNum();
    }

    public void stop() {
        stopped = true;
        //int round_num = Clock.getRoundNum();
        int left = Clock.getBytecodesLeft();
        elaps += start - left;
    }

    public void restart() {
        stopped = true;
        elaps = 0;
    }

    @Override
    public String toString() {
        return "BC:" + elaps;
    }
}
