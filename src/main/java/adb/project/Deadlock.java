package adb.project;

import java.util.*;

class Deadlock{
	Map<String, Set<String>> graph;
	
	void addEdge(String t1, String t2){
		if(!graph.containsKey(t1)){
			Set<String> toTransactions = new HashSet<String>();
            toTransactions.add(t2);
            graph.put(t1, toTransactions);
		}
		else{
			graph.get(t1).add(t2);
		}
	}
	void removeVertex(String t){
		if(graph.containsKey(t))
			graph.remove(t);
		for (Set<String> toTransactions : graph.values()){
			if(toTransactions.contains(t)){
				toTransactions.remove(t);
			}
		}
	}

	List<String> findCycle(){
		List<String> cycle = new ArrayList<>();
		String cycle_start = "";
		String cycle_end = "";
		Map<String, Integer> color = new HashMap<String, Integer>();
		Map<String, String> parent = new HashMap<String, String>();
		for (String tid : graph.keySet()){
			color.put(tid, 0);
			parent.put(tid, "");
			for (String tid2: graph.get(tid)){
				color.put(tid2, 0);
				parent.put(tid2, "");
			}
		}
		for (String tid : graph.keySet()){
			if(color.get(tid) == 0 && dfs(tid, cycle_start, cycle_end, color, parent))
				break;
		}
		if(cycle_start.equals("")){
			return cycle;
		}
		else{
			cycle.add(cycle_start);
			for (String t = cycle_end; !t.equals(cycle_start); t = parent.get(t)){
				cycle.add(t);
			}
			cycle.add(cycle_start);
			return cycle;
		}

	}

	boolean dfs(String t, String cycle_start, String cycle_end, Map<String, Integer> color, Map<String, String> parent) {
    color.put(t, 1);
    for (String t2 : graph.get(t)) {
        if (color.get(t2) == 0) {
            parent.put(t, t2);
            if (dfs(t, cycle_start, cycle_end, color, parent))
                return true;
        } else if (color.get(t2) == 1) {
            cycle_end = t;
            cycle_start = t2;
            return true;
        }
    }
    color.put(t, 2);
    return false;
	}

}