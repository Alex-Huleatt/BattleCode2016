/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package team018.frameworks.util;

/**
 *
 * @author alexhuleatt
 * @param <A>
 * @param <B>
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class Pair<A extends Comparable,B extends Comparable> implements Comparable {
    public A a;
    public B b;
    
    public Pair(A a, B b) {
        this.a=a;
        this.b=b;
    }
    
    @Override
    public String toString() {
        return a.toString() +", " + b.toString();
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Pair<?,?>)) {
            return 0;
        } else {
            Pair p = (Pair) o;
            int res = a.compareTo(p.a);
            if (res == 0) {
                return b.compareTo(p.b);
            } else {
                return res;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair<?, ?> pair = (Pair<?, ?>) o;

        if (a != null ? !a.equals(pair.a) : pair.a != null) return false;
        return !(b != null ? !b.equals(pair.b) : pair.b != null);

    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        return result;
    }
}
