package adb.project;

import java.util.*;

public class DataManager {
    Map<Integer, List<Integer>> varLocations;
    List<Site> sites;
    int totalVars;
    int totalSites;

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
    }

    ReadLockResponse readVal(int var, String transaction) {
        List<Integer> locationList = varLocations.get(var);
        ReadLockResponse response = null;
        for (Integer siteId : locationList) {
            Site site = sites.get(siteId.intValue() - 1);
            if (site.isUp()) {
                ReadLockResponse siteResponse = site.readVal(var, transaction);
                // if any response granted or safe, then return safe, o/w return unsafe
                if (siteResponse.isGranted() || !siteResponse.isUnsafe()) {
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
        for (Integer siteId : locationList) {
            Site site = sites.get(siteId.intValue() - 1);
            if (site.isUp()) {
                WriteLockResponse siteResponse = site.writeVal(var, transaction, val);
                if (siteResponse.isGranted()) {
                    // do nothing
                } else {
                    isNegativeResponse = true;
                    if (!siteResponse.isUnsafe()) {
                        anySafe = true;
                        guiltyTransactionIds.addAll(siteResponse.getGuiltyTransactionIds());
                    }
                }
            }
        }
        if (!isNegativeResponse) {
            return new WriteLockResponse(true, false, null);
        } else {
            if (anySafe) {
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

    void fail(int siteId) {
        sites.get(siteId - 1).fail();
    }

    void recover(int siteId) {
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
        sites.get(siteId - 1).recover(safeVars);
    }

}