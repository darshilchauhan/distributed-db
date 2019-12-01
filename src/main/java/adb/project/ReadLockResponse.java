package adb.project;

public class ReadLockResponse {
    boolean granted;
    boolean unsafe;
    int val;
    String guiltyTransactionId;

    ReadLockResponse(boolean granted, boolean unsafe, int val, String guiltyTransactionId) {
        this.granted = granted;
        this.unsafe = unsafe;
        this.val = val;
        this.guiltyTransactionId = guiltyTransactionId;
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

    String getGuiltyTransactionId() {
        return guiltyTransactionId;
    }
}