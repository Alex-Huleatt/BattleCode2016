package team018.frameworks;

import battlecode.common.MapLocation;
import team018.frameworks.util.Common;

/**
 * Created by alexhuleatt on 1/13/16.
 */
public class UnitTests {

    public static void main(String[] args) {
        for (int i = -100; i <= 100; i++) {
            for (int j=-100; j <=100; j++) {
                MapLocation m = new MapLocation(i,j);
                if (!Common.intToLoc(Common.locToInt(m)).equals(m)) {
                    System.out.println(m + " " + Integer.toBinaryString(Common.locToInt(m)));
                }
            }
        }
    }
}
