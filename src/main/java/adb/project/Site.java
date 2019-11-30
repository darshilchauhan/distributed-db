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

        // for readlock, set will be empty if var is not readlocked
        readLockTable = new HashMap<Integer, Set<String>>();
        for (Integer var : designatedVars) {
            readLockTable.put(var, new HashSet<String>());
        }

        // for writelocktable, string will be empty if not writelocked
        writeLockTable = new HashMap<Integer, String>();
        for (Integer var : designatedVars) {
            writeLockTable.put(var, "");
        }

        readLockInfo = new HashMap<String, List<Integer>>();
        writeLockInfo = new HashMap<String, List<Integer>>();
    }

    // if writelocked, then return true or false depending on who holds the lock
    // if readlocked by this transaction, then return true
    // otherwise, assign lock and return true
    boolean getReadLoack(int var, String transaction) {
        if (writeLockTable.containsKey(var)) {
            if (writeLockTable.get(var).equals(transaction))
                return true;
            else
                return false;
        } else if (readLockTable.containsKey(var) && readLockTable.get(var).contains(transaction)) {
            return true;
        } else {
            readLockTable.get(var).add(transaction);
            if (!readLockInfo.containsKey(transaction)) {
                List<Integer> newList = new ArrayList<Integer>();
                newList.add(var);
                readLockInfo.put(transaction, newList);
            } else {
                readLockInfo.get(transaction).add(var);
            }
            return true;
        }
    }

    // if writelocked, then return true or false depending on who holds the lock
    // if readlocked by this transaction and no other transaction, then update to
    // writelock and return true
    // otherwise, assign writelock and return true
    boolean getWriteLock(int var, String transaction) {
        if (writeLockTable.containsKey(var)) {
            if (writeLockTable.get(var).equals(transaction))
                return true;
            else
                return false;
        } else if (readLockTable.containsKey(var)) {
            if (readLockTable.get(var).contains(transaction) && readLockTable.get(var).size() == 1) {
                // update readlock to write lock
                readLockTable.get(var).clear();
                writeLockTable.put(var, transaction);
                return true;
            } else {
                return false;
            }
        } else {
            writeLockTable.put(var, transaction);
            return true;
        }
    }

}