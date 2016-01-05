package team018.FrWorks.movement.pathplanning;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import battlecode.common.MapLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Alex
 */
public class AStar {

    private static final double sqrt2 = 1.41421356237;
    //Using hashes instad of arrays for overall memory savings on sparse grids.
    //Minor memory losses on dense grids. <25%.
    //Java hashmap loadfactor to resize = ~.75
    private final HashSet<MapLocation> obs; //set of obstacles
    private final HashMap<MapLocation, Integer> prev; //Map for pointing to parent
    private final HashMap<MapLocation, Integer> cost; //Map holding cost of point

    private final LocIntHeap q = new LocIntHeap(3000); //Allocate early.
    private MapLocation dest;
    public static final int MAX_PATH_LENGTH = 100;
    private final MapLocation[] temp = new MapLocation[MAX_PATH_LENGTH]; //Allocate this one time.
    public int minX, minY, maxX, maxY;

    public final MapLocation[] path_buffer;

    /**
     *
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     * @param obs
     */
    public AStar(int minX, int maxX, int minY, int maxY, HashSet<MapLocation> obs) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.obs = obs;
        this.prev = new HashMap<MapLocation, Integer>();
        this.cost = new HashMap<MapLocation, Integer>();
        this.path_buffer = null;
    }

    /**
     *
     * @param minX
     * @param maxX
     * @param minY
     * @param maxY
     * @param obs HashSet of obstacles
     * @param path_buffer Array to hold path after reconstruction.
     */
    public AStar(int minX, int maxX, int minY, int maxY, HashSet<MapLocation> obs, MapLocation[] path_buffer) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.obs = obs;
        this.prev = new HashMap<MapLocation, Integer>();
        this.cost = new HashMap<MapLocation, Integer>();
        this.path_buffer = path_buffer;
    }

    public AStar(HashSet<MapLocation> obs) {
        this.obs = obs;
        this.minX = Integer.MIN_VALUE;
        this.minY = Integer.MIN_VALUE;
        this.maxX = Integer.MAX_VALUE;
        this.maxY = Integer.MAX_VALUE;
        this.prev = new HashMap<MapLocation, Integer>();
        this.cost = new HashMap<MapLocation, Integer>();
        this.path_buffer = null;
    }

    public AStar() {
        this.obs = new HashSet<MapLocation>();
        this.minX = Integer.MIN_VALUE;
        this.minY = Integer.MIN_VALUE;
        this.maxX = Integer.MAX_VALUE;
        this.maxY = Integer.MAX_VALUE;
        this.prev = new HashMap<MapLocation, Integer>();
        this.cost = new HashMap<MapLocation, Integer>();
        this.path_buffer = null;
    }

    /**
     * Finds a path from some start to some finish.
     *
     * @param start
     * @param finish
     * @return An array of MapLocations representing a path that does not
     * intersect any obstacles.
     */
    public MapLocation[] pathfind(MapLocation start, MapLocation finish) {
        prev.clear();
        cost.clear();

        cost.put(finish, 0);
        MapLocation current;
        dest = start;
        final int desx = dest.x;
        final int desy = dest.y;
        for (int i = 7; i != -1; --i) {
            check(finish, i);
        }
        while (!q.isEmpty()) {
            current = q.pop();
            if (current.x == desx && current.y == desy) {
                return reconstruct();
            }
            expand(current);
        }
        return null;
    }

    /**
     * Reconstruct the path.
     *
     * @return
     */
    private MapLocation[] reconstruct() {

        MapLocation current = dest;
        int count = 0;
        int dir = 0;
        int next;
        do {
            next = ((prev.get(current) + 4) & 7);
            if (dir == 0 || next != dir) { //this minimizes the path.
                temp[count++] = current;
                dir = next;
            }
            current = moveTo(current, next);
        } while (prev.containsKey(current));
        temp[count++] = current;
        if (path_buffer != null && path_buffer.length > count) {
            System.arraycopy(temp, 0, path_buffer, 0, count);
            Arrays.fill(path_buffer, count, path_buffer.length, null);
            return path_buffer;
        }
        final MapLocation[] path = new MapLocation[count];
        System.arraycopy(temp, 0, path, 0, count);
        return path;
    }

    /**
     * This takes a single point and checks all it's neighbors for viability.
     *
     * @param p MapLocation (Vertex) to expand.
     */
    private void expand(MapLocation p) {

        final int dir = prev.get(p);
        if (!(p.x < maxX && p.y < maxY && p.x >= minX && p.y >= minY)) {
            return;
        }
        check(p, dir); //+0
        if ((dir & 1) == 1) { //is diagonal
            check(p, ((dir + 6) & 7)); //-2
            check(p, ((dir + 7) & 7)); //-1
            check(p, ((dir + 1) & 7)); //+1
            check(p, ((dir + 2) & 7)); //+2
        } else {
            check(p, ((dir + 7) & 7)); //-1
            check(p, ((dir + 1) & 7)); //+1
        }
    }

    /**
     * Checks a child vertex to potentially set it's value and add to queue.
     * Uses distance (see point for definition of "distance" from point to goal
     * as heuristic.
     *
     * @param parent Parent vertex
     * @param dir direction to some child of parent.
     */
    private void check(MapLocation parent, int dir) {
        final MapLocation n = moveTo(parent, dir);

        int potentialCost = cost.get(parent) + dist(parent, n);
        if (!prev.containsKey(n)) { //has no parent, has not been considered
            int dis = dist(n, dest);

            q.add(n, dis + potentialCost);
            prev.put(n, dir);
            cost.put(n, potentialCost);
        }
    }

    /**
     * Octile distance rounded down. Euclidean distance squared does not satisfy
     * the triangle inequality and is not a valid metric. This is really meant
     * for path planning, where this is "good enough" Didn't want to calculate
     * square root. 3Expensive5Me.
     *
     * @param p1
     * @param p2
     * @return
     */
    public int dist(MapLocation p1, MapLocation p2) {
        int tx = Math.abs(p1.x - p2.x);
        int ty = Math.abs(p1.y - p2.y);
        int tp = Math.min(tx, ty); //number of diagonals
        double tp2 = sqrt2 * tp + (tx + ty) - tp;
        return (int) Math.round(tp2); //close enough for my needs.

    }

    /**
     * Moves a MapLocation one cell along direction d. Ugly but fast.
     *
     * @param p
     * @param d
     * @return
     */
    private static MapLocation moveTo(MapLocation p, int d) {
        switch (d) {
            case 0:
                return new MapLocation(p.x, p.y - 1);
            case 1:
                return new MapLocation(p.x + 1, p.y - 1);
            case 2:
                return new MapLocation(p.x + 1, p.y);
            case 3:
                return new MapLocation(p.x + 1, p.y + 1);
            case 4:
                return new MapLocation(p.x, p.y + 1);
            case 5:
                return new MapLocation(p.x - 1, p.y + 1);
            case 6:
                return new MapLocation(p.x - 1, p.y);
            default:
                return new MapLocation(p.x - 1, p.y - 1);
        }
    }

    /**
     * New obstacle will be considered on next run. If you modify the original
     * obs HashSet, that also works. This allows you to not maintain a reference
     * to it.
     *
     * @param p
     */
    public void addObs(MapLocation p) {
        obs.add(p);
    }

    /**
     * This will decompress a path, adding in all the MapLocations between It's
     * relatively expensive so use only when you think units will be off the
     * path So that PathMove can get them back on using a raycast
     *
     * @param compressed
     * @return
     */
    public static MapLocation[] decompressPath(MapLocation[] compressed) {
        MapLocation[] buffered = new MapLocation[200];
        MapLocation next = compressed[0];
        buffered[0] = next;
        int count = 1;
        int compressed_MapLocationer = 1;
        while (!next.equals(compressed[compressed.length - 1]) && count < buffered.length) {
            next.add(compressed[compressed_MapLocationer - 1].directionTo(compressed[compressed_MapLocationer]));
            if (next.equals(compressed[compressed_MapLocationer])) {
                compressed_MapLocationer++;
            }
            buffered[count++] = next;
        }
        MapLocation[] actual = new MapLocation[count];
        System.arraycopy(buffered, 0, actual, 0, count);
        return actual;
    }

}
