package FrWorks.movement.potential;

import FrWorks.util.Point;

import java.util.PriorityQueue;

/**
 * Created by alexhuleatt on 12/14/15.
 */
public class Field2 {

    private double[][] local_field;

    private final int width;
    private final int height;

    public Field2(int width, int height) {
        this.width = width;
        this.height = height;
        local_field = new double[width][height];
    }

    private void fix(int lowX, int lowY, int highX, int highY) {
        PriorityQueue<Point> sources = new PriorityQueue<>();
        Point<Double> temp;
        for (int i = lowX; i <= highX; i++) {
            temp = new Point(i,lowY);
            temp.cost = readField(i,lowY);
            sources.add(temp);
            temp = new Point(i,highY);
            temp.cost = readField(i,highY);
            sources.add(temp);
        }
        for (int i = lowY; i <= highY; i++) {
            temp = new Point(lowX, i);
            temp.cost = readField(lowX,i);
            sources.add(temp);
            temp = new Point(highX, i);
            temp.cost = readField(highX,i);
            sources.add(temp);
        }

        for (int i = lowX+1; i < highX; i++) {
            for (int j = lowY+1; j < highY; j++) {
                double cost = sourceCost(i,j);
                if (cost != 0) {
                    temp = new Point(i,j);
                    temp.cost = cost;
                    sources.add(temp);
                }
            }
        }

        while (!sources.isEmpty()) {

        }

    }

    public double sourceCost(int x, int y) {
        return 0.0;
    }



    public double readField(int x, int y) {
        return 0.0; //return the (potentially wrong) field value for this location.
    }

    public double determineField(int x, int y) {
        return 0.0; //determine what the field value *should* be.
    }

    public void setField(int x, int y, double val) {
        //set the field's value at x, y to val.
    }

    public Point move(Point m, int dir) {
        switch (dir) {
            case 0:
                return new Point(m.x,m.y-1);
            case 1:
                return new Point(m.x+1,m.y+1);
            case 2:
                return new Point(m.x+1,m.y);
            case 3:
                return new Point(m.x+1,m.y+1);
            case 4:
                return new Point(m.x,m.y+1);
            case 5:
                return new Point(m.x-1,m.y+1);
            case 6:
                return new Point(m.x-1,m.y);
            case 7:
                return new Point(m.x-1,m.y-1);
            default:
                return m;
        }
    }
}
