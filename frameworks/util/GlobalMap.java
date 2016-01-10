package team018.frameworks.util;

import java.util.HashMap;

/**
 * I am so sorry.
 * Created by alexhuleatt on 1/10/16.
 */
public class GlobalMap {

    private static final HashMap<String, Object> map = new HashMap<>();


    public static final void put(String key, Object val) {
        map.put(key,val);
    }

    /**
     * This is not safe.
     * I regret nothing
     * @param key
     * @param <E>
     * @return
     */
    public static final  <E> E get(String key) {
        return (E)map.get(key);
    }

}
