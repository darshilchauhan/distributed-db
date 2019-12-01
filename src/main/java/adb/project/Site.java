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

    // restrictions:
    // commitedVals must always have all designated vars as key (it will always have
    // some value)
    // readLockTable must always have all designated vars as key (with value empty
    // list if no readlocks)
    // writeLockTable must always have all designated vars as key (with value empty
    // string if no writelock)

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
        resetReadLockTable();

        // for writelocktable, string will be empty if not writelocked
        writeLockTable = new HashMap<Integer, String>();
        resetWriteLockTable();

        readLockInfo = new HashMap<String, List<Integer>>();
        writeLockInfo = new HashMap<String, List<Integer>>();
    }

    void resetReadLockTable() {
        readLockTable.clear();
        for (Integer var : designatedVars) {
            readLockTable.put(var, new HashSet<String>());
        }
    }

    void resetWriteLockTable() {
        writeLockTable.clear();
        for (Integer var : designatedVars) {
            writeLockTable.put(var, "");
        }
    }

    int readVal(int var) {
        return commitedVals.getOrDefault(var, Integer.MIN_VALUE);
    }

    void writeVal(int var, int val) {
        commitedVals.put(var, val);
        if (!safeVars.contains(var))
            safeVars.add(var);
    }

    // if writelocked, then return true or false depending on who holds the lock
    // if readlocked by this transaction, then return true
    // otherwise, assign lock and return true
    boolean getReadLoack(int var, String transaction) {
        if (!safeVars.contains(var))
            return false;
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
        if (!safeVars.contains(var))
            return false;
        if (writeLockTable.containsKey(var)) {
            if (writeLockTable.get(var).equals(transaction))
                return true;
            else
                return false;
        } else if (readLockTable.containsKey(var)) {
            if (readLockTable.get(var).contains(transaction) && readLockTable.get(var).size() == 1) {
                // upgrade readlock to write lock
                readLockTable.get(var).clear();
                writeLockTable.put(var, transaction);
                // update readLockInfo and writeLockInfo
                readLockInfo.get(transaction).remove(Integer.valueOf(var));
                // using Integer.valueOf so it deletes by object not index
                writeLockInfo.get(transaction).add(var);
                return true;
            } else {
                return false;
            }
        } else {
            writeLockTable.put(var, transaction);
            writeLockInfo.get(transaction).add(var);
            return true;
        }
    }

    // // release a single lock from both lockTable and lockInfo
    // void releaseReadLock(int var, String transaction) {
    // readLockTable.get(var).remove(transaction);
    // readLockInfo.get(transaction).remove(Integer.valueOf(var));
    // }

    // // release a single lock from both lockTable and lockInfo
    // void releaseWriteLock(int var, String transaction) {
    // writeLockTable.put(var, "");
    // writeLockInfo.get(transaction).remove(Integer.valueOf(var));
    // }

    // // Note: below 2 functions don't use above 2 functions
    // // release all locks from both lockTable and lockInfo
    // void clearReadLocks(String transaction) {
    // for (Integer var : readLockInfo.get(transaction)) {
    // readLockTable.get(var).remove(transaction);
    // }
    // readLockInfo.get(transaction).clear();
    // }

    // // release all locks from both lockTable and lockInfo
    // void clearWriteLocks(String transaction) {
    // for (Integer var : writeLockInfo.get(transaction)) {
    // writeLockTable.put(var, "");
    // }
    // readLockInfo.get(transaction).clear();
    // }

    // to be called during end of transaction, to release all locks and info
    void clearTransaction(String transaction) {
        for (Integer var : readLockInfo.get(transaction)) {
            readLockTable.get(var).remove(transaction);
        }
        for (Integer var : writeLockInfo.get(transaction)) {
            writeLockTable.put(var, "");
        }
        readLockInfo.remove(transaction);
        writeLockInfo.remove(transaction);
    }

    void fail() {
        isUp = false;
        resetReadLockTable();
        resetWriteLockTable();
        readLockInfo.clear();
        writeLockInfo.clear();
    }

    void recover(Set<Integer> newSafeVars) {
        isUp = true;
        safeVars.clear();
        safeVars.addAll(newSafeVars);
    }

}