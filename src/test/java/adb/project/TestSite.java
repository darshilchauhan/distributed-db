package adb.project;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestSite {
    @Test
    public void testRead() {
        Site site = new Site(2);
        assertTrue(site.readLockTable.containsKey(1));
        assertEquals(site.readLockTable.get(1).size(), 0);
        assertEquals(site.commitedVals.getOrDefault(1, 0).intValue(), 10);
        assertEquals(site.readVal(1), 10);

        ReadLockResponse response = site.readVal(1, "T1");
        assertTrue(response.isGranted());
        assertEquals(response.isUnsafe(), false);
        assertEquals(response.getVal(), 10);

        assertEquals(site.readLockTable.get(1).size(), 1);
        assertTrue(site.readLockTable.get(1).contains("T1"));
        assertEquals(site.readLockInfo.size(), 1);
        assertTrue(site.readLockInfo.containsKey("T1"));
        assertEquals(site.readLockInfo.get("T1").get(0).intValue(), 1);
    }

    @Test
    public void testSiteWrite() {
        Site site = new Site(2);
        WriteLockResponse response = site.writeVal(1, "T1", 35);
        assertTrue(response.isGranted());
        assertFalse(response.isUnsafe());
    }

}