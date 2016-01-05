/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team018.frameworks.util;

import battlecode.common.MapLocation;

/**
 *
 * @author alexhuleatt
 */
public class Geom {

    /**
     * Returns the euclidean distance squared between loc, and the line defined
     * by start -> end.
     *
     * @param start
     * @param end
     * @param loc
     * @return
     */
    public static double disToLine(MapLocation start, MapLocation end, MapLocation loc) {
        int x1 = start.x;
        int y1 = start.y;
        int x2 = end.x;
        int y2 = end.y;
        int x0 = loc.x;
        int y0 = loc.y;
        int xdiff = x2 - x1;
        int ydiff = y2 - y1;
        int n = ydiff * x0 - xdiff * y0 + x2 * y1 - y2 * x1;

        return (n * n) / (ydiff * ydiff + xdiff * xdiff);
    }

    public static MapLocation midpoint(MapLocation start, MapLocation end) {
        return new MapLocation((start.x + end.x) / 2, (start.y + end.y) / 2);
    }

    //The following code was shamelessly taken and modified for battlecode from
    //http://martin-thoma.com/how-to-check-if-two-line-segments-intersect/
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    public static boolean isPointOnLine(MapLocation a1, MapLocation a2, MapLocation b) {
        MapLocation offsetB = new MapLocation(a2.x - a1.x, a2.y - a1.y);

        MapLocation bTmp = new MapLocation(b.x - a1.x, b.y - a1.y);
        double r = crossProduct(offsetB, bTmp);
        return Math.abs(r) < .001;
    }

    private static double crossProduct(MapLocation a, MapLocation b) {
        return a.x * b.y - b.x * a.y;
    }

    private static MapLocation[] getBoundingBox(MapLocation a1, MapLocation a2) {
        return new MapLocation[]{
            new MapLocation(Math.min(a1.x, a2.x), Math.min(a1.y, a2.y)),
            new MapLocation(Math.min(a1.x, a2.x), Math.max(a1.y, a2.y)),
            new MapLocation(Math.max(a1.x, a2.x), Math.min(a1.y, a2.y)),
            new MapLocation(Math.max(a1.x, a2.x), Math.max(a1.y, a2.y))};
    }

    private static boolean doBoundingBoxesIntersect(MapLocation[] a, MapLocation[] b) {
        return a[0].x <= b[1].x
                && a[1].x >= b[0].x
                && a[0].y <= b[1].y
                && a[1].y >= b[0].y;
    }

    private static boolean isPointRightOfLine(MapLocation a1, MapLocation a2, MapLocation b) {
        // Move the image, so that a.first is on (0|0)
        MapLocation t = new MapLocation(a2.x - a1.x, a2.y - a1.y);
        MapLocation t2 = new MapLocation(b.x - a1.x, b.y - a1.y);
        return crossProduct(t, t2) < 0;
    }

    private static boolean lineSegmentTouchesOrCrossesLine(MapLocation a1, MapLocation a2, MapLocation b1, MapLocation b2) {
        return isPointOnLine(a1, a2, b1)
                || isPointOnLine(a1, a2, b2)
                || (isPointRightOfLine(a1, a2, b1)
                ^ isPointRightOfLine(a1, a2, b2));
    }

    public static boolean doLinesIntersect(MapLocation a1, MapLocation a2, MapLocation b1, MapLocation b2) {

        MapLocation[] box1 = getBoundingBox(a1, a2);
        MapLocation[] box2 = getBoundingBox(b1, b2);
        return doBoundingBoxesIntersect(box1, box2)
                && lineSegmentTouchesOrCrossesLine(a1, a2, b1, b2)
                && lineSegmentTouchesOrCrossesLine(b1, b2, a1, a2);
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public static boolean locOnLine(MapLocation start, MapLocation end, MapLocation loc) {
        if (start == null || end == null || loc == null) {
            // YOU DONE MESSED UP A-A-RON
            return false;
        }
        return disToLine(start, end, loc) < 4;
    }

}
