package adb.project;

public class OperationBE extends Operation {
    String transactionId;
    boolean readOnly;

    OperationBE(char type, int beginTime, String transactionId, boolean readOnly) {
        super(type, beginTime);
        this.transactionId = transactionId;
        this.readOnly = readOnly;
    }

    @Override
    String getTransactionId() {
        return transactionId;
    }

    @Override
    boolean isReadOnly() {
        return readOnly;
    }
}