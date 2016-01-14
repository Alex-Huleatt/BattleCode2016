/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team018.frameworks.util;

/**
 *
 * @author alexhuleatt
 * @param <K>
 * @param <E>
 */
@SuppressWarnings("unchecked")
public class TinyHash<K, E> {

    private final Object[][] keys;
    private final Object[][] vals;
    private final int len;
    private final int cap;
    private final int s_cap;

    public TinyHash(int cap, int s_cap) {
        keys = new Object[cap][s_cap];
        vals = new Object[cap][s_cap];
        len = 0;
        this.cap = cap;
        this.s_cap = s_cap;
    }

    public int len() {
        return len;
    }

    public int cap() {
        return cap;
    }

    public int s_cap() {
        return s_cap;
    }

    /**
     * O(s_cap)
     *
     * @param k
     * @param e
     * @return
     */

    public boolean put(K k, E e) {
        int h1 = k.hashCode() % cap;
        int n_index = next_index(h1);
        if (n_index == -1) {
            return false;
        }
        keys[h1][n_index] = k;
        vals[h1][n_index] = e;
        return true;
    }

    private int next_index(int h) {
        for (int i = 0; i < s_cap; i++) {
            if (vals[h][i] == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * O(s_cap)
     *
     * @param k
     * @return
     */
    public E get(K k) {
        int h1 = k.hashCode() % cap;
        for (int i = 0; i < s_cap; i++) {
            if (k.equals(keys[h1][i])) {
                return (E) vals[h1][i];
            }
        }
        return null;
    }

    public double load_factor() {
        return ((double) len) / cap;
    }
}
