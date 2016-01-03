/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FrWorks.comm;

import FrWorks.util.Common;
import FrWorks.util.Rand;
import battlecode.common.Clock;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import java.util.HashMap;
import java.util.Objects;

/**
 *
 * @author alexhuleatt
 */
public class RadioController {

    private final RobotController rc;

    private final HashMap<String, Integer> channelMap;

    private final HashMap<String, RadioQueue> queues;

    public RadioController(RobotController rc) {
        this.rc = rc;
        this.channelMap = new HashMap<>();
        this.queues = new HashMap<>();
    }

    public Message readMessage(String channel) throws Exception {
        int chan = getChannel(channel);
        int raw = rc.readBroadcast(chan);
        if (!isSigned(raw, Clock.getRoundNum())) {
            return Message.UNSIGNED;
        }
        int unsigned = unsign(raw, Clock.getRoundNum());
        Message[] allM = Message.values();
        if (unsigned < 0 || unsigned > allM.length) {
            System.out.println("Error in RadioController.readMessage, message was signed but out of bounds.");
            return Message.ERROR;
        }
        return allM[unsigned];
    }

    public MapLocation readLoc(String channel) throws Exception {
        int chan = getChannel(channel);
        int raw = rc.readBroadcast(chan);
        if (!isSigned(raw, Clock.getRoundNum())) {
            return null;
        }
        int unsigned = unsign(raw, Clock.getRoundNum());
        return Common.intToLoc(unsigned);
    }

    public int readInt(String channel) throws Exception {
        int chan = getChannel(channel);
        int raw = rc.readBroadcast(chan);
        if (!isSigned(raw, Clock.getRoundNum())) {
            return Integer.MIN_VALUE;
        }
        int unsigned = unsign(raw, Clock.getRoundNum());
        return unsigned;
    }

    public void write(String channel, Message m) throws Exception {
        int chan = getChannel(channel);
        rc.broadcast(chan, sign(m.ordinal(), Clock.getRoundNum()));
    }

    public void write(String channel, MapLocation m) throws Exception {
        int chan = getChannel(channel);
        rc.broadcast(chan, sign(Common.locToInt(m), Clock.getRoundNum()));
    }

    public void write(String channel, int n) throws Exception {
        int chan = getChannel(channel);
        rc.broadcast(chan, sign(n, Clock.getRoundNum()));
    }

    int getChannel(String channel) {
        if (channelMap.containsKey(channel)) {
            return channelMap.get(channel);
        }
        byte[] barr = channel.getBytes();
        long s = 0;
        for (int i = 0; i < barr.length; i++) { //hash the string.
            s = s ^ Rand.xorshiftstar(barr[i]);
        }
        int ch = (int) (((s % GameConstants.BROADCAST_MAX_CHANNELS)
                + GameConstants.BROADCAST_MAX_CHANNELS)
                % GameConstants.BROADCAST_MAX_CHANNELS);
        channelMap.put(channel, ch);
        return ch;
    }

    private static int sign(int message, int round_num) {
        return message | getMask(round_num);
    }

    private static boolean isSigned(int message, int round_num) {
        int mask = getMask(round_num);
        return (message & mask) == mask;
    }

    private static int unsign(int message, int round_num) {
        return message ^ getMask(round_num);
    }

    public static int getMask(int round_num) {
        return Clock.getRoundNum() << 24;
    }


    /**
     * TODO
     */
    private class RadioQueue {

        private final String name;

        public RadioQueue(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RadioQueue other = (RadioQueue) obj;
            return Objects.equals(this.name, other.name);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 23 * hash + Objects.hashCode(this.name);
            return hash;
        }

        /**
         * TODO
         * @param loc 
         */
        public void push(MapLocation loc) {

        }

        /**
         * TODO
         * @param n 
         */
        public void push(int n) {

        }

        /**
         * TODO
         * @param m 
         */
        public void push(Message m) { }

        /**
         * TODO
         * @param off
         * @return 
         */
        public MapLocation getLoc(int off) {
            return null;
        }

        /**
         * TODO
         * @param off
         * @return 
         */
        public int getInt(int off) {
            return Integer.MIN_VALUE;
        }

        /**
         * TODO
         * @param off
         * @return 
         */
        public Message getMessage(int off) {
            return null;
        }

        private int head() {
            return getChannel(name);
        }
        public int len() {
            return head() + 1;
        }
        private int tail() {
            return head()+len();
        }
    }

    /**
     * Clear queue.
     *
     * @param channel
     */
    public void resetQueue(String channel) {

    }

    public void push(String channel, int n) {
        final RadioQueue rq;
        if (!queues.containsKey(channel)) {
            rq = new RadioQueue(channel);
        } else {
            rq = queues.get(channel);
        }
        rq.push(n);
    }

    public void push(String channel, MapLocation m) {
        final RadioQueue rq;
        if (!queues.containsKey(channel)) {
            rq = new RadioQueue(channel);
        } else {
            rq = queues.get(channel);
        }
        rq.push(m);
    }

    public void push(String channel, Message m) {
        final RadioQueue rq;
        if (!queues.containsKey(channel)) {
            rq = new RadioQueue(channel);
        } else {
            rq = queues.get(channel);
        }
        rq.push(m);
    }

    public int getInt(String channel, int index) {
        final RadioQueue rq;
        if (queues.containsKey(channel)) {
            rq = queues.get(channel);
        } else {
            System.out.println("Error in RadioController.getInt, no such channel exists");
            return Integer.MIN_VALUE;
        }
        return rq.getInt(index);

    }

    public MapLocation getLoc(String channel, int index) {
        final RadioQueue rq;
        if (queues.containsKey(channel)) {
            rq = queues.get(channel);
        } else {
            System.out.println("Error in RadioController.getLoc, no such channel exists");
            return null;
        }
        return rq.getLoc(index);
    }

    public Message getMessage(String channel, int index) {
        final RadioQueue rq;
        if (queues.containsKey(channel)) {
            rq = queues.get(channel);
        } else {
            System.out.println("Error in RadioController.getMessage, no such channel exists");
            return Message.ERROR;
        }
        return rq.getMessage(index);

    }
    
    public int queueLen(String channel) {
        if (queues.containsKey(channel)) {
            return queues.get(channel).len();
        } else {
            System.out.println("Error in RadioController.queueLen, no queue by that name exists");
            return Integer.MIN_VALUE;
        }
    }


    //Potential field stuff down here
    //TODO

    public int getCellInfo(MapLocation m) {
        return 0;
    }

    public void setCellInfo(MapLocation m , int info) {

    }
}
