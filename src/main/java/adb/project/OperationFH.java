package adb.project;

public class OperationFH extends Operation {
    int siteId;

    OperationFH(char type, int beginTime, int siteId) {
        super(type, beginTime);
        this.siteId = siteId;
    }

    @Override
    int getSiteId() {
        return siteId;
    }
}