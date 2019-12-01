package adb.project;

import java.util.*;

public class WriteLockResponse {
    boolean granted;
    boolean unsafe;
    List<String> guiltyTransactionIds;

    WriteLockResponse(boolean granted, boolean unsafe, List<String> guiltyTransactionIds) {
        this.granted = granted;
        this.unsafe = unsafe;
        this.guiltyTransactionIds = guiltyTransactionIds;
    }

    boolean isGranted() {
        return granted;
    }

    boolean isUnsafe() {
        return unsafe;
    }

    List<String> getGuiltyTransactionIds() {
        return guiltyTransactionIds;
    }

    void addGuiltyTransactionId(String transactionId) {
        this.guiltyTransactionIds.add(transactionId);
    }
}