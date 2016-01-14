package team018.frameworks.util;

import java.util.HashMap;

/**
 * Created by alexhuleatt on 1/13/16.
 */
@SuppressWarnings("unchecked")
public class GlobalMap {

    public static HashMap<String, Object> map = new HashMap<>();

    public static void put(String key, Object value) {map.put(key,value);}

    public static <K> K get(String key) {return (K)map.get(key);}

    public static boolean containsKey(String key) {
        return map.containsKey(key);
    }
}
