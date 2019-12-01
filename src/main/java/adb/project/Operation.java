package adb.project;

public abstract class Operation {
    int timeStamp;
    char type;

    Operation(char type, int timestamp) {
        this.type = type;
        this.timeStamp = timestamp;
    }

    char getType() {
        return type;
    }

    int getTimeStamp() {
        return timeStamp;
    }

    String getTransactionId() {
        return "";
    }

    int getSiteId() {
        return 0;
    }

    int getVar() {
        return 0;
    }

    int getVal() {
        return 0;
    }
}