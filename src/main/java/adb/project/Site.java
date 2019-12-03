package adb.project;

import java.util.*;

class Site {
    int id;
    boolean isUp;
    int lastRecoverTime;
    int lastFailTime;
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
        this.lastRecoverTime = -1;
        this.lastFailTime = -2;

        designatedVars = new HashSet<Integer>();
        for (int var = 1; var <= totalVars / 2; var++) {
            designatedVars.add(var * 2);
        }
        if (id % 2 == 0) {
            designatedVars.add(id - 1);
            designatedVars.add(id + 9);
            // if (id == 2)
            // System.out.println(" designated vars of site 2: " + designatedVars);
        }

        commitedVals = new HashMap<Integer, Integer>();
        for (Integer var : designatedVars) {
            commitedVals.put(var, 10 * var);
            // if (id == 3)
            // System.out.println("--- " + var);
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

    Set<Integer> getDesignatedVars() {
        return designatedVars;
    }

    boolean isUp() {
        return isUp;
    }

    int getId() {
        return id;
    }

    int getLastRecoverTime() {
        return lastRecoverTime;
    }

    int getLastFailTime() {
        return lastFailTime;
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

    // check if var is in designatedVars before calling
    void writeValDirectly(int var, int val) {
        commitedVals.put(var, val);
    }

    // if writelocked, then return true or false depending on who holds the lock
    // if readlocked by this transaction, then return true
    // otherwise, assign lock and return true
    ReadLockResponse readVal(int var, String transaction) {
        ReadLockResponse yesResponse = new ReadLockResponse(true, false, readVal(var), "");
        if (!safeVars.contains(var)) {
            return new ReadLockResponse(false, true, 0, "");
        }
        if (writeLockTable.containsKey(var) && !writeLockTable.get(var).equals("")) {
            if (writeLockTable.get(var).equals(transaction)) {
                return yesResponse;
            } else {
                return new ReadLockResponse(false, false, 0, writeLockTable.get(var));
            }
        } else if (readLockTable.containsKey(var) && readLockTable.get(var).contains(transaction)) {
            return yesResponse;
        } else {
            readLockTable.get(var).add(transaction);
            if (!readLockInfo.containsKey(transaction)) {
                List<Integer> newList = new ArrayList<Integer>();
                newList.add(var);
                readLockInfo.put(transaction, newList);
            } else {
                readLockInfo.get(transaction).add(var);
            }
            return yesResponse;
        }
    }

    // if writelocked, then return true or false depending on who holds the lock
    // if readlocked by this transaction and no other transaction, then update to
    // writelock and return true
    // otherwise, assign writelock and return true
    WriteLockResponse writeVal(int var, String transaction, int val) {
        WriteLockResponse yesResponse = new WriteLockResponse(true, false, null);
        if (!safeVars.contains(var)) {
            // System.out.println("Unsafe");
            return new WriteLockResponse(false, true, null);
        }
        if (writeLockTable.containsKey(var) && !writeLockTable.get(var).equals("")) {
            if (writeLockTable.get(var).equals(transaction)) {
                // System.out.println("already write lock");
                return yesResponse;
            } else
                // System.out.println("Some other write lock");
                return new WriteLockResponse(false, false,
                        new ArrayList<String>(Arrays.asList(writeLockTable.get(var))));
        } else if (readLockTable.containsKey(var) && readLockTable.get(var) != null
                && !readLockTable.get(var).isEmpty()) {
            if (readLockTable.get(var).contains(transaction) && readLockTable.get(var).size() == 1) {
                // upgrade readlock to write lock
                readLockTable.get(var).clear();
                writeLockTable.put(var, transaction);
                // update readLockInfo and writeLockInfo
                readLockInfo.get(transaction).remove(Integer.valueOf(var));
                // using Integer.valueOf so it deletes by object not index

                // update writeLockInfo
                if (!writeLockInfo.containsKey(transaction)) {
                    List<Integer> newList = new ArrayList<Integer>();
                    newList.add(var);
                    writeLockInfo.put(transaction, newList);
                } else {
                    writeLockInfo.get(transaction).add(var);
                }

                // System.out.println("Upgrade from readLock. Yes.");
                return yesResponse;
            } else {
                List<String> guiltyTransactionIds = new ArrayList<String>();
                for (String transactionId : readLockTable.get(var)) {
                    if (transaction.equals(transactionId)) // if current transaction is in the list
                        continue;
                    else
                        guiltyTransactionIds.add(transaction);
                }
                // System.out.println("Other readLocks. No.");
                return new WriteLockResponse(false, false, guiltyTransactionIds);
            }
        } else {
            writeLockTable.put(var, transaction);
            if (!writeLockInfo.containsKey(transaction)) {
                List<Integer> newList = new ArrayList<Integer>();
                newList.add(var);
                writeLockInfo.put(transaction, newList);
            } else {
                writeLockInfo.get(transaction).add(var);
            }
            // System.out.println("No locks. Yes.");
            return yesResponse;
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
        if (readLockInfo.get(transaction) != null) {
            for (Integer var : readLockInfo.get(transaction)) {
                readLockTable.get(var).remove(transaction);
            }
        }
        if (writeLockInfo.get(transaction) != null) {
            for (Integer var : writeLockInfo.get(transaction)) {
                writeLockTable.put(var, "");
            }
        }
        readLockInfo.remove(transaction);
        writeLockInfo.remove(transaction);
    }

    void fail(int tick) {
        isUp = false;
        lastFailTime = tick;
        resetReadLockTable();
        resetWriteLockTable();
        readLockInfo.clear();
        writeLockInfo.clear();
    }

    void recover(Set<Integer> newSafeVars, int tick) {
        isUp = true;
        lastRecoverTime = tick;
        safeVars.clear();
        safeVars.addAll(newSafeVars);
    }

    String dumpValues() {
        StringBuilder answer = new StringBuilder("site " + id + " - ");
        List<Integer> vars = new ArrayList<>(commitedVals.keySet());
        Collections.sort(vars);
        for (int i = 0; i < vars.size(); i++) {
            if (i != vars.size() - 1) {
                answer.append("x" + vars.get(i) + ": " + commitedVals.get(vars.get(i)) + ", ");
            } else {
                answer.append("x" + vars.get(i) + ": " + commitedVals.get(vars.get(i)));
            }
        }
        return answer.toString();
    }

}