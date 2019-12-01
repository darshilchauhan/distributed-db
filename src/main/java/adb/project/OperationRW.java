package adb.project;

public class OperationRW extends Operation {
    String transactionId;
    int var;
    int val;

    OperationRW(char type, int beginTime, String transactionId, int var, int val) {
        super(type, beginTime);
        this.transactionId = transactionId;
        this.var = var;
        this.val = val;
    }

    @Override
    String getTransactionId() {
        return transactionId;
    }

    @Override
    int getVar() {
        return var;
    }

    @Override
    int getVal() {
        return val;
    }
}