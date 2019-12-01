package adb.project;

public class OperationBE extends Operation {
    String transactionId;

    OperationBE(char type, int beginTime, String transactionId) {
        super(type, beginTime);
        this.transactionId = transactionId;
    }

    @Override
    String getTransactionId() {
        return transactionId;
    }
}