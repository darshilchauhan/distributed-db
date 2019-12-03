package adb.project;

import java.util.*;

public class DataManager {
    Map<Integer, List<Integer>> varLocations;
    List<Site> sites;
    int totalVars;
    int totalSites;
    Map<String, List<Integer>> accessedSites;

    DataManager() {
        totalVars = 20;
        totalSites = 10;

        varLocations = new HashMap<Integer, List<Integer>>();
        for (int var = 1; var <= totalVars; var++) {
            List<Integer> locationList = new ArrayList<Integer>();
            if (var % 2 == 1) {
                locationList.add((var % 10) + 1);
            } else {
                for (int i = 1; i <= totalSites; i++) {
                    locationList.add(i);
                }
            }
            // if (var == 1)
            // System.out.println(Arrays.toString(locationList.toArray()));
            varLocations.put(var, locationList);
        }

        sites = new ArrayList<Site>();
        for (int i = 1; i <= totalSites; i++) {
            Site site = new Site(i);
            sites.add(site);
        }

        accessedSites = new HashMap<String, List<Integer>>();
    }

    ReadLockResponse readVal(int var, String transaction) {
        List<Integer> locationList = varLocations.get(var);
        ReadLockResponse response = new ReadLockResponse(false, false, 0, null);
        for (Integer siteId : locationList) {
            Site site = sites.get(siteId.intValue() - 1);
            if (site.isUp()) {
                ReadLockResponse siteResponse = site.readVal(var, transaction);
                // if any response granted or safe, then return safe, o/w return unsafe
                if (siteResponse.isGranted() || !siteResponse.isUnsafe()) {
                    if (siteResponse.isGranted()) {
                        if (!accessedSites.get(transaction).contains(siteId.intValue()))
                            accessedSites.get(transaction).add(siteId);
                    }
                    return siteResponse;
                } else {
                    response = siteResponse;
                }
            }
        }
        return response;

    }

    WriteLockResponse writeVal(int var, String transaction, int val) {
        List<Integer> locationList = varLocations.get(var);
        boolean isNegativeResponse = false;
        boolean anySafe = false;
        List<String> guiltyTransactionIds = new ArrayList<String>();
        boolean anyUp = false;
        for (Integer siteId : locationList) {
            Site site = sites.get(siteId.intValue() - 1);
            if (site.isUp()) {
                anyUp = true;
                WriteLockResponse siteResponse = site.writeVal(var, transaction, val);
                // System.out.println("for writing x" + var + ", site " + site.getId() + " is
                // up");
                if (siteResponse.isGranted()) {
                    if (!accessedSites.get(transaction).contains(siteId.intValue()))
                        accessedSites.get(transaction).add(siteId.intValue());
                } else {
                    isNegativeResponse = true;
                    if (!siteResponse.isUnsafe()) {
                        anySafe = true;
                        guiltyTransactionIds.addAll(siteResponse.getGuiltyTransactionIds());
                    }
                }
            }
        }
        if (anyUp && !isNegativeResponse) {
            return new WriteLockResponse(true, false, null);
        } else {
            if (anyUp && anySafe) {
                return new WriteLockResponse(false, false, guiltyTransactionIds);
            } else {
                return new WriteLockResponse(false, true, null);
            }
        }
    }

    void clearTransaction(String transaction) {
        for (Site site : sites) {
            site.clearTransaction(transaction);
        }
    }

    void fail(int siteId, int tick) {
        sites.get(siteId - 1).fail(tick);
    }

    void recover(int siteId, int tick) {
        Set<Integer> safeVars = new HashSet<Integer>();
        boolean anySiteUp = false;
        for (Site site : sites) {
            if (site.isUp())
                // will return false for with siteId=siteId
                anySiteUp = true;
        }

        if (anySiteUp) {
            // only add corresponding odd vars
            safeVars.add(siteId - 1);
            safeVars.add(siteId + 9);
        } else {
            // add all vars
            for (Integer var : sites.get(siteId - 1).getDesignatedVars()) {
                safeVars.add(var.intValue());
            }
        }
        sites.get(siteId - 1).recover(safeVars, tick);
    }

    void beginTransactionRW(String transactionId) {
        accessedSites.put(transactionId, new ArrayList<Integer>());
    }

    boolean canCommit(String transactionId, int beginTime) {
        List<Integer> transactionSites = accessedSites.get(transactionId);
        for (Integer siteId : transactionSites) {
            // System.out.println("begintime of " + transactionId + " is " + beginTime);
            // System.out.println("lastfailtime of " + siteId + " is " + sites.get(siteId -
            // 1).lastFailTime);
            if (!sites.get(siteId - 1).isUp() || sites.get(siteId - 1).lastFailTime > beginTime) {
                return false;
            }
        }
        return true;
    }

    void abort(String transactionId) {
        List<Integer> transactionSites = accessedSites.get(transactionId);
        for (Integer siteId : transactionSites) {
            sites.get(siteId - 1).clearTransaction(transactionId);
        }
        accessedSites.remove(transactionId);
    }

    void commit(String transactionId, Map<Integer, Integer> modifiedVals) {
        List<Integer> transactionSites = accessedSites.get(transactionId);
        // System.out.println("TransactionSites: " +
        // Arrays.toString(transactionSites.toArray()));
        // System.out.println("modifiedVals KeySet: " + modifiedVals.keySet());
        // System.out.println("modifiedVal for x1: " + modifiedVals.get(1));
        for (Integer siteId : transactionSites) {
            Site site = sites.get(siteId - 1);
            // System.out.println("vars for site " + siteId + " " +
            // site.getDesignatedVars());
            for (Integer modifiedVar : modifiedVals.keySet()) {
                if (site.getDesignatedVars().contains(modifiedVar)) {
                    // System.out.println("Writing directly x" + modifiedVar + " to " +
                    // modifiedVals.get(modifiedVar));
                    site.writeValDirectly(modifiedVar, modifiedVals.get(modifiedVar));
                }
            }
            site.clearTransaction(transactionId);
        }
        accessedSites.remove(transactionId);
    }

    String dumpValues() {
        StringBuilder answer = new StringBuilder();
        for (Site site : sites) {
            answer.append(site.dumpValues() + "\n");
        }
        return answer.toString();
    }

}