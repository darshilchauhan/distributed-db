package adb.project;

public class OperationFH extends Operation {
    int siteId;

    OperationFH(char type, int timestamp, int siteId) {
        super(type, timestamp);
        this.siteId = siteId;
    }

    @Override
    int getSiteId() {
        return siteId;
    }
}