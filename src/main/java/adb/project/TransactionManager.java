package adb.project;

import java.util.*;

public class TransactionManager {
    DataManager dm;
    Map<String, Transaction> transactionMap;
    List<String> transactionList;
    List<Operation> operationQ;
    // DeadLock deadLock;
    String inputFileString;
    boolean readFromQ; // assign true if last operation was 'E', assign false when read from file
    Integer indexInQ; // not NULL only when readFromQ, used to keep track of which op to remove

    TransactionManager(String inputFileString) {
        this.inputFileString = inputFileString;
        dm = new DataManager();
        transactionMap = new HashMap<String, Transaction>();
        transactionList = new LinkedList<String>();
        operationQ = new ArrayList<Operation>();
        // deadlock = new DeadLock();
    }

    void process(Operation op) {
        // check if it can be executed
        switch (op.getType()) {
        case 'B':
            Transaction transaction = new Transaction(op.getTransactionId(), op.getTimeStamp());
            transactionMap.put(op.getTransactionId(), transaction);
            transactionList.add(op.getTransactionId());
            break;
        case 'E':
            dm.clearTransaction(op.getTransactionId());
            // TODO: commit and abort logic
            break;
        case 'F':
            dm.fail(op.getSiteId());
            break;
        case 'H':
            dm.recover(op.getSiteId());
            break;
        case 'R':
            ReadLockResponse readResponse = dm.readVal(op.getVar(), op.getTransactionId());
            if (readResponse.isGranted()) {
                System.out.println("x" + Integer.toString(op.getVar()) + ": " + readResponse.getVal());
            } else if (!readResponse.isUnsafe()) {
                operationQ.add(op);
                // deadlock.addEdge(op.getTransactionId(),readResponse.getGuiltyTransactionId());
            } else {
                // TODO: what to do if can't read because of unsafe?
                // for now, skipping the operation
            }
            break;
        case 'W':
            WriteLockResponse writeResponse = dm.writeVal(op.getVar(), op.getTransactionId(), op.getVal());
            if (writeResponse.isGranted()) {
                System.out.println("written value x" + Integer.toString(op.getVar()) + ": " + op.getVal());
            } else if (!writeResponse.isUnsafe()) {
                operationQ.add(op);
                // for (String guiltyTransactionId : writeResponse.getGuiltyTransactionIds()) {
                // deadlock.addEdge(op.getTransactionId(), guiltyTransactionId);
                // }
            } else {
                // TODO: what to do if can't write because of unsafe?
                // for now, skipping the operation
            }
            break;
        default:
            break;
        }
        // if yes, execute and remove from q if readFromQ
        // if no, do nothing if read from q, add to q otherwise
    }

    Operation getNextOperation() {
        // read from Q if applicable, otherwise call readNextEvent
        if (readFromQ) {
            if (indexInQ == null)
                indexInQ = 0;
            else
                indexInQ++;
            if (indexInQ >= operationQ.size()) {
                readFromQ = false;
                return getNextOperation();
            } else {
                return operationQ.get(indexInQ);
            }
        } else {
            return readNextEvent();
        }
    }

    Operation readNextEvent() {
        return null;
    }

    void processNextOperation() {
        Operation op = getNextOperation();
        process(op);
    }
}