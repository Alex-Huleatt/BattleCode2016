/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FrWorks.util;

import java.util.Iterator;

/**
 *
 * @author alexhuleatt
 * @param <K>
 */
@SuppressWarnings("unchecked")
public class PopArray<K> implements Iterable<K> {
    
    int[] indices;
    int indices_index;
    Object[] vals;
    int len;
    int cap;
    
    public PopArray(int cap) {
        this.cap = cap;
        vals=new Object[cap];
        indices=new int[cap];
        indices_index=-1;
        len=0;
    }
    
    public int add(K k) {
        int dex;
        if (indices_index == -1) { //list is contiguous
            dex = len;
            vals[dex]= k;
        } else {
            dex = indices[indices_index--];
            vals[dex] = k;
        }
        len++;
        return dex;
    }

    public K pop(int index) {
        indices[indices_index++]=index;
        len--;
        K k = (K)vals[index];
        vals[index]=null;
        return k;
    }
    
    public K get(int index) {
        return (K)vals[index];
    }
    
    public int len() {
        return len;
    }
    
    public int cap() {
        return cap;
    }

    @Override

    public Iterator<K> iterator() {
        return new Iterator<K>() {
            int in = 0;
            
            @Override
            public boolean hasNext() {
                return in < len-1;
            }

            @Override
            public K next() {
                int dex = in;
                while (vals[dex]==null && dex < len) {
                    dex++;
                }
                return (K)vals[dex];
            }
            
        };
    }
    
    
}
