package adb.project;

import java.util.*;

class Site {
    int id;
    boolean isUp;
    int totalVars;
    Set<Integer> designatedVars;
    Map<Integer, Integer> commitedVals;
    Set<Integer> safeVars;
    Map<Integer, Set<String>> readLockTable;
    Map<Integer, String> writeLockTable;
    Map<String, List<Integer>> readLockInfo;
    Map<String, List<Integer>> writeLockInfo;

    Site(int id) {
        totalVars = 20;
        this.id = id;
        this.isUp = true;

        designatedVars = new HashSet<Integer>();
        for (int var = 1; var <= totalVars / 2; var++) {
            designatedVars.add(var * 2);
        }
        designatedVars.add(this.id - 1);
        designatedVars.add(this.id + 9);

        commitedVals = new HashMap<Integer, Integer>();
        for (Integer var : designatedVars) {
            commitedVals.put(var, 10 * var);
        }

        safeVars = new HashSet<Integer>();
        for (Integer var : designatedVars) {
            safeVars.add(var.intValue());
        }

        readLockTable = new HashMap<Integer, Set<String>>();
        writeLockTable = new HashMap<Integer, String>();
        readLockInfo = new HashMap<String, List<Integer>>();
        writeLockInfo = new HashMap<String, List<Integer>>();
    }

}