package adb.project;

import java.util.*;

public class ReadLockResponse {
    boolean granted;
    boolean unsafe;
    int val;
    List<String> guiltyTransactionIds;

    ReadLockResponse(boolean granted, boolean unsafe, int val, List<String> guiltyTransactionIds) {
        this.granted = granted;
        this.unsafe = unsafe;
        this.val = val;
        this.guiltyTransactionIds = guiltyTransactionIds;
    }

    boolean isGranted() {
        return granted;
    }

    boolean isUnsafe() {
        return unsafe;
    }

    int getVal() {
        return val;
    }

    List<String> getGuiltyTransactionIds() {
        return guiltyTransactionIds;
    }
}