/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team018.frameworks.util;

/**
 *
 * @author alexhuleatt
 * @param <E>
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class Point<E extends Comparable> implements Comparable {

    public final int x;
    public final int y;
    public E cost;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Octile distance rounded down. Euclidean distance squared does not satisfy
     * the triangle inequality and is not a valid metric. This is really meant
     * for path planning, where this is "good enough" Didn't want to calculate
     * square root. 3Expensive5Me.
     *
     * @param p
     * @return
     */
    public double dist(Point p) {
        int dx = p.x - x;
        int dy = p.y - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    @Override
    public boolean equals(Object o) {
        final Point t;
        if (o instanceof Point) {
            t = (Point) o;
            return (t.x == x && t.y == y);
        }
        return false;
    }

    public boolean equals(Point p) {
        return (p.x == x && p.y == y);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + this.x;
        hash = 37 * hash + this.y;
        return hash;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + "):" + cost;
    }

    @Override

    public int compareTo(Object o) {
        if (!(o instanceof Point<?>)) {
            return 0;
        }
        return cost.compareTo( ((Point<E>) o).cost );
    }
}
