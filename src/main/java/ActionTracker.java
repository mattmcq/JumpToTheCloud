import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class ActionTracker {

    // Assumption: times are all integers; if they are not we could instead use BigDecimals but that would be a performance hit
    // Assumption: assume all json input is well formed and uses only specified 'action' and 'time' values; otherwise build in a bunch of try and catching

    HashMap<String, Double> averages = new HashMap<>();
    MultiValuedMap<Object, Object> values = new ArrayListValuedHashMap<>();

    public synchronized void addAction(String jsonAction) {  // synchronized so multiple threads don't modify averages and values at the same time
        JSONObject event = new JSONObject(jsonAction);
        values.put(event.get("action"), event.get("time"));
        calculateAverages(values);
    }

    private void calculateAverages(MultiValuedMap<Object, Object> values) {
        for (Object action : values.keySet()) {
            int total = 0;
            Object[] times = values.get(action).toArray();
            for (int i = 0; i < times.length; i++) {
                Integer val = (Integer) times[i];
                total = total + val;
            }
            double average = (double) total / times.length;
            averages.put((String) action, average);
        }
    }

    public String getStats() {
        JSONArray results = new JSONArray();
        TreeMap<String, Double> sortedAverages = new TreeMap<>(averages); // 'jump' should be above 'run' when printed

        for (Map.Entry<String, Double> entry : sortedAverages.entrySet()) {
            JSONObject obj = new JSONObject();
            obj.put("action", entry.getKey());
            obj.put("avg", entry.getValue());
            results.put(obj);
        }

        return String.valueOf(results);
    }


}
