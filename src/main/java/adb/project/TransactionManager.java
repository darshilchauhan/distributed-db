package adb.project;

import java.io.*;
import java.util.*;

public class TransactionManager {
    int tick;
    DataManager dm;
    Map<String, Transaction> transactionMap;
    List<String> transactionList;
    List<Operation> operationQ;
    // DeadLock deadLock;
    BufferedReader reader;
    boolean readFromQ; // assign true if last operation was 'E', assign false when read from file
    Integer indexInQ; // not NULL only when readFromQ, used to keep track of which op to remove

    TransactionManager(String inputFileString) {
        tick = 0;
        dm = new DataManager();
        transactionMap = new HashMap<String, Transaction>();
        transactionList = new LinkedList<String>();
        operationQ = new ArrayList<Operation>();
        try{
            reader = new BufferedReader(new FileReader(inputFileString));
        }
        catch (FileNotFoundException fe){
            System.out.println("Exception thrown:" + fe);
        }
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
        // TODO
        // if yes, execute and remove from q if readFromQ
        // if no, do nothing if read from q, add to q otherwise
    }

    // get next operation either from queue or from file
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

    // read from file
    Operation readNextEvent() {
        String line = readNextLine();
        String[] op = line.split("[()]");
        if (op[0].equals("begin")) {
            return new OperationBE('B', tick++, op[1], false);
        } else if (op[0].equals("beginRO")) {
            return new OperationBE('B', tick++, op[1], false);
        } else if (op[0].equals("end")) {
            return new OperationBE('E', tick++, op[1], false);
        } else if (op[0].equals("R")) {
            String[] splits = op[1].split(",");
            return new OperationRW('R', tick++, splits[0], Integer.parseInt(splits[1]), 0);
        } else if (op[0].equals("W")) {
            String[] splits = op[1].split(",");
            return new OperationRW('W', tick++, splits[0], Integer.parseInt(splits[1]), Integer.parseInt(splits[2]));
        } else if (op[0].equals("fail")) {
            return new OperationFH('F', tick++, Integer.parseInt(op[1]));
        } else if (op[0].equals("recover")) {
            return new OperationFH('H', tick++, Integer.parseInt(op[1]));
        } else if (op[0].equals("dump")) {
            // dumpValues();
            tick++;
            return readNextEvent();
        }
        return null;
    }

    // get next line from file
    String readNextLine() {
        String line = this.reader.readLine();
        return line;
    }

    // this will be called from main in a loop
    void processNextOperation() {
        Operation op = getNextOperation();
        process(op);
    }
}