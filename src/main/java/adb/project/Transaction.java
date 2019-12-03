package adb.project;

import java.util.*;

class Transaction {
    String id;
    int beginTime;
    Map<Integer, Integer> modifiedVals;

    Transaction(String id, int beginTime) {
        this.id = id;
        this.beginTime = beginTime;
        modifiedVals = new HashMap<Integer, Integer>();
    }

    String getId() {
        return this.id;
    }

    int getBeginTime() {
        return this.beginTime;
    }

    Map<Integer, Integer> getModifiedVals() {
        return modifiedVals;
    }

    void putModifiedVal(int var, int val) {
        modifiedVals.put(var, val);
    }

    boolean hasModifiedVal(int var) {
        return modifiedVals.containsKey(var);
    }

    int getModifiedVal(int var) {
        return modifiedVals.getOrDefault(var, Integer.MIN_VALUE);
    }

    void clearModifiedVals() {
        modifiedVals.clear();
    }
}