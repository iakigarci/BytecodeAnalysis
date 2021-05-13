package callgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalledFromList {
    private Map<String,List<String>> calledMap;
    private static CalledFromList calledFromList = null;
    
    private CalledFromList() {
        calledMap = new HashMap<>();
    }

    public Map<String, List<String>> getCalledMap() {
        return calledMap;
    }

    public static CalledFromList getCalledfromlist() {
        if (calledFromList == null) {
            calledFromList = new CalledFromList();
        }
        return calledFromList;
    }
    
    public void add(String method) {
        calledMap.put(method, new ArrayList<String>());
    }

    public void addToList(String key, String val) {  // key llamado por val
        if (!calledMap.containsKey(key)) {
            calledMap.put(key, new ArrayList<String>());
        }
        List<String> l = calledMap.get(key);
        l.add(val);
        calledMap.put(key, l);
    }
}

