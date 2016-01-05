/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team018.FrWorks.movement.pathplanning;

import team018.FrWorks.util.Pair;
import team018.FrWorks.util.Point;
import java.util.ArrayList;

import java.util.PriorityQueue;

/**
 *
 * @author alexhuleatt
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class DSLite {

    private final int[][] parent;
    private final boolean[][] obs;
    private final Pair<Double, Double>[][] costs; //g cost and rhs cost
    public static final double INFINITY = Double.MAX_VALUE;
    public final PriorityQueue<Point> q;
    private Point sgoal;
    private Point sstart;
    int x, y;

    public DSLite(int x, int y) {
        this.x = x;
        this.y = y;
        this.obs = new boolean[x][y];
        //g_costs = new HashMap<>();
        //rhs_costs = new HashMap<>();
        q = new PriorityQueue<>();
        parent = new int[x][y];
        costs = new Pair[x][y];
    }

    public void addObstacle(Point p) {
        //idk how D* Lite handles removal of edges, so we're just going
        //to increase their value. *shrug*
        ArrayList<Point> neigh = succ(p);
        obs[p.x][p.y] = true;
        for (Point n : neigh) {
            updateState(n);
        }
        updateState(p);
    }

    public void initialize(Point start, Point end) {
        this.sgoal = new Point(end.x, end.y);

        this.sstart = new Point(start.x, start.y);
        //starting vals.
        Pair<Double, Double> sstart_cost = new Pair<>(INFINITY, INFINITY);
        costs[sstart.x][sstart.y] = sstart_cost;

        Pair<Double, Double> sgoal_cost = new Pair<>(INFINITY, 0.0);
        costs[sgoal.x][sgoal.y] = sgoal_cost;

        sgoal.cost = key(sgoal);
        q.add(sgoal);
        resolve(sstart);
    }

    public void resolve(Point ss) {
        Point current;

        while ((key(q.peek())
                .compareTo(key(ss)) < 0
                || !costs[ss.x][ss.y].b.equals(costs[ss.x][ss.y].a))) {
            current = q.poll();
            Pair<Double, Double> cost = costs[current.x][current.y];
            double current_g = cost.a;
            double current_rhs = cost.b;
            Point p;
            if (current_g > current_rhs) {
                costs[current.x][current.y].a = current_rhs;
                updatePredRHS(current);

                for (int i = 0; i < 8; i++) {
                    p = moveTo(current, i);
                    updateState(p);
                }
            } else {
                costs[current.x][current.y].a = INFINITY;
                updatePredRHS(current);

                for (int i = 0; i < 8; i++) {
                    p = moveTo(current, i);
                    updateState(p);
                }
                updateState(current);
            }
        }

    }

    public ArrayList<Point> pathfind(Point start) {
        resolve(start);
        Point current = start;
        ArrayList<Point> path = new ArrayList<>();
        while (!current.equals(sgoal)) {
            path.add(current);
            current = moveTo(current, (parent[current.x][current.y] + 4) & 7);
        }
        return path;
    }

    private Pair<Double, Double> key(Point<Pair<Double, Double>> p) {
        Pair<Double, Double> cost = costs[p.x][p.y];
        double first = Math.min(
                cost.a,
                cost.b);

        return new Pair<>(first + p.dist(sstart), first);
    }

    private double resetRHS(Point p) {
        if (p.equals(sgoal)) {
            return 0;
        }
        double min = INFINITY;
        double cost;
        int min_point = 0;
        for (int i = 0; i < 8; i++) {
            Point n = moveTo(p, i);
            if (isValid(n) && costs[n.x][n.y] != null) {
                double g_cost = costs[n.x][n.y].a;

                cost = g_cost + ((obs[p.x][p.y] || obs[n.x][n.y]) ? INFINITY : p.dist(n));
                min = Math.min(min, cost);
                min_point = i;
            }
        }
        parent[p.x][p.y] = min_point;
        return min;
    }

    private void updatePredRHS(Point p) {
        Pair<Double, Double> p_cost = costs[p.x][p.y];
        double myCost = p_cost.a;
        for (int i = 0; i <= 8; i++) {
            Point n = moveTo(p, i);
            if (isValid(n)) {
                Pair<Double, Double> cost = costs[n.x][n.y];
                if (cost == null) {
                    cost = new Pair<>(INFINITY, INFINITY);
                }
                if (obs[p.x][p.y] || moveTo(p, parent[n.x][n.y]).equals(n)) {
                    cost.b = resetRHS(n);
                } else {
                    double poss = myCost + p.dist(n);
                    if (poss < cost.b) {
                        cost.b = poss;
                    }
                }
            }
        }

    }

    private void updateState(Point p) {
        if (!isValid(p)) {
            return;
        }
        Pair<Double, Double> cost = costs[p.x][p.y];
        if (cost == null) {
            costs[p.x][p.y] = (cost = new Pair<>(INFINITY, INFINITY));

            updatePredRHS(p);
        }
        if (!p.equals(sgoal)) {
            cost.b = resetRHS(p);
        }
        if (q.contains(p)) {
            q.remove(p);
        }
        if (!(cost.a.equals(cost.b))) {
            p.cost = key(p);
            q.add(p);
        }

    }

    private boolean isValid(Point p) {
        return !(p.x < 0 || p.x >= x || p.y < 0 || p.y >= y);
    }

    private ArrayList<Point> succ(Point p) {
        Pair<Double, Double> cost = costs[p.x][p.y];
        if (cost == null) {
            ArrayList<Point> succ = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                Point n = moveTo(p, i);
                if (isValid(n) && !obs[n.x][n.y]) {
                    succ.add(n);
                }
            }
            return succ;
        } else {
            int dir = parent[p.x][p.y];
            ArrayList<Point> ret = new ArrayList<>();
            int[] dirs;
            if ((dir & 1) == 1) {
                dirs = new int[]{0, 6, 7, 1, 2};
            } else {
                dirs = new int[]{0, 7, 1};
            }
            for (int i = 0; i < dirs.length; i++) {
                Point n = moveTo(p, (dir + dirs[i]) & 7);
                if (isValid(n) && !obs[n.x][n.y]) {
                    ret.add(n);
                }
            }
            return ret;
        }

    }

    private static Point moveTo(Point p, int d) {
        switch (d) {
            case 0:
                return new Point(p.x, p.y - 1);
            case 1:
                return new Point(p.x + 1, p.y - 1);
            case 2:
                return new Point(p.x + 1, p.y);
            case 3:
                return new Point(p.x + 1, p.y + 1);
            case 4:
                return new Point(p.x, p.y + 1);
            case 5:
                return new Point(p.x - 1, p.y + 1);
            case 6:
                return new Point(p.x - 1, p.y);
            default:
                return new Point(p.x - 1, p.y - 1);
        }
    }

}
