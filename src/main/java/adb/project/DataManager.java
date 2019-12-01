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
        }

        sites = new ArrayList<Site>();
        for (int i = 1; i <= totalSites; i++) {
            Site site = new Site(i);
            sites.add(site);
        }
    }

    boolean getReadLock(int var, String transaction) {
        List<Integer> locationList = varLocations.get(var);
        for (Integer siteId : locationList) {
            Site site = sites.get(siteId);
            if (site.getReadLoack(var, transaction))
                return true;
        }
        return false;
    }

    boolean getWriteLock(int var, String transaction) {
        List<Integer> locationList = varLocations.get(var);
        for (Integer siteId : locationList) {
            Site site = sites.get(siteId);
            if (site.getWriteLock(var, transaction))
                return true;
        }
        return false;
    }

    void clearTransaction(String transaction) {
        for (Site site : sites) {
            site.clearTransaction(transaction);
        }
    }

    void fail(int siteId) {
        sites.get(siteId).fail();
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
            for (Integer var : sites.get(siteId).getDesignatedVars()) {
                safeVars.add(var.intValue());
            }
        }
        sites.get(siteId).recover(safeVars);
    }

}