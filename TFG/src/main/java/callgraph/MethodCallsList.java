package callgraph;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.checkerframework.checker.units.qual.m;

public class MethodCallsList {
    private static MethodCallsList mCallsList;
    private List<HashMap<MethodReport, List<MethodReport>>> methodCalls;

    private MethodCallsList() {
        methodCalls = new ArrayList<HashMap<MethodReport, List<MethodReport>>>();
    }

    public static MethodCallsList getMethodCallsList() {
        if (mCallsList == null) {
            mCallsList = new MethodCallsList();
        }
        return mCallsList;
    }

    public List<HashMap<MethodReport, List<MethodReport>>> getMethodCalls() {
        return methodCalls;
    }

    public void add(HashMap<MethodReport, List<MethodReport>> map) {
        methodCalls.add(map);
    }
    
    public MethodReport getKey(MethodReport method, HashMap<MethodReport, List<MethodReport>> map) {
        for (Map.Entry<MethodReport, List<MethodReport>> entry : map.entrySet()) {
            if (entry.getKey().equals(method)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
