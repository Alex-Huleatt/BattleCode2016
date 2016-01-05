/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team018.FrWorks.util;

/**
 *
 * @author alexhuleatt
 */
public class Rand {

    public static long xorshiftstar(int x) {
        x ^= x >> 12;
        x ^= x << 25;
        x ^= x >> 27;
        return x * 2685821657736338717l;
    }

    public int hash(int seed, int max) {
        long rand = Math.abs(1 + xorshiftstar(seed));
        return (int) (rand % max);
    }

}
