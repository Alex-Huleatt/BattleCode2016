/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team018.frameworks.movement.pathplanning;

import battlecode.common.MapLocation;

/**
 * A quick and dirty array-based binary min-heap implementation, designed for
 * use as a priority queue. Not dynamic, it is very fast though.
 *
 * @author alexhuleatt
 */
public class LocIntHeap {

    public final int cap;
    private int index;
    private final MapLocation[] points;
    private final int[] costs;

    public LocIntHeap(int cap) {
        this.cap = cap;
        points = new MapLocation[cap];
        costs = new int[cap];
        index = 0;
    }

    @Override
    public String toString() {
        String str = "[";
        for (int i = 0; i < index - 1; i++) {
            str += points[i] + ":" + costs[i] + " ";
        }
        if (index > 0) {
            str += points[index - 1] + ":" + costs[index - 1];
        }
        str += "]";
        return str;
    }

    public void add(MapLocation p, int cost) {
        int pos = index++;
        for (; pos > 0 && cost < costs[pos / 2]; pos /= 2) { //heapify
            costs[pos] = costs[pos / 2];
            points[pos] = points[pos / 2];
        }
        costs[pos] = cost;
        points[pos] = p;
    }

    public MapLocation pop() {
        if (index == 0) {
            return null;
        }
        final MapLocation min_point = points[0];
        --index;
        if (index == 0) {
            return min_point;
        }
        points[0] = points[index];
        costs[0] = costs[index];

        int r = 0;
        int child = r * 2 + 1;
        while (child < index) {
            if (costs[child] > costs[child + 1]) {
                child++;
            }
            if (costs[child] < costs[r]) {
                swap(r, child);
                r = child;
                child *= 2;
            } else {
                return min_point;
            }
            ++child;
        }
        return min_point;
    }

    public boolean isEmpty() {
        return (index == 0);
    }

    private void swap(int a, int b) {
        MapLocation temp_pair = points[a];
        int temp_cost = costs[a];

        points[a] = points[b];
        points[b] = temp_pair;

        costs[a] = costs[b];
        costs[b] = temp_cost;
    }

    public void clear() {
        index = 0;
    }

    public void decrease_key(MapLocation p, int new_cost) {
        decrease_key(getIndex(p), new_cost);
    }

    public void decrease_key(int dex, int new_cost) {
        costs[dex] = new_cost;
        while (costs[dex / 2] > new_cost) {
            swap(dex, dex / 2);
            dex = dex / 2;
        }

    }

    public void increase_key(MapLocation p, int new_cost) {
        increase_key(getIndex(p), new_cost);
    }

    public void increase_key(int pos, int new_cost) {
        for (; pos > 0 && new_cost < costs[pos / 2]; pos /= 2) { //heapify
            costs[pos] = costs[pos / 2];
            points[pos] = points[pos / 2];
        }
    }

    private int getIndex(MapLocation p) {
        MapLocation sp;
        for (int i = 0; i < index; i++) {
            sp = points[i];
            if (p.x == sp.x && p.y == sp.y) {
                return i;
            }
        }
        return -1;
    }

}
