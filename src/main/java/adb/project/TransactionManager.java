package adb.project;

import java.io.*;
import java.util.*;

public class TransactionManager {
    int tick;
    DataManager dm;
    StringBuilder output;
    Map<String, Transaction> transactionMap;
    List<String> transactionList;
    List<Operation> operationQ;
    Deadlock deadlock;
    BufferedReader reader;
    boolean readFromQ; // assign true if last operation was 'E', assign false when read from file
    Integer indexInQ; // not NULL only when readFromQ, used to keep track of which op to remov

    TransactionManager(String inputFileString) {
        tick = 0;
        dm = new DataManager();
        output = new StringBuilder("");
        transactionMap = new HashMap<String, Transaction>();
        transactionList = new LinkedList<String>();
        operationQ = new ArrayList<Operation>();
        try {
            reader = new BufferedReader(new FileReader(inputFileString));
        } catch (FileNotFoundException fe) {
            System.out.println("Exception thrown:" + fe);
        }
        deadlock = new Deadlock();
        readFromQ = false;
        indexInQ = null;
    }

    boolean breakCycle() {
        List<String> cycle = deadlock.findCycle();
        if (!cycle.isEmpty()) {
            // System.out.println("cycle found");
            String youngest = cycle.get(0);
            int youngestTime = transactionMap.get(youngest).getBeginTime();
            for (int i = 1; i < cycle.size(); i++) {
                if (transactionMap.get(cycle.get(i)).getBeginTime() > youngestTime) {
                    youngest = cycle.get(i);
                    youngestTime = transactionMap.get(cycle.get(i)).getBeginTime();
                }
            }
            deadlock.removeVertex(youngest);
            abortTransaction(youngest, "Deadlock");
            breakCycle();
            return true;
        }
        return false;
    }

    void abortTransaction(String transactionId, String reason) {
        for (int i = 0; i < operationQ.size(); i++) {
            Operation opTemp = operationQ.get(i);
            if (opTemp.getTransactionId().equals(transactionId)) {
                operationQ.remove(i);
                i--;
            }
        }
        dm.abort(transactionId);
        output.append(transactionId + " aborts\n");
        output.append("Reason for abort: " + reason + "\n");
    }

    boolean process(Operation op) {
        // System.out.println("Inside process " + op.getType());
        boolean result = true;
        switch (op.getType()) {
        case 'B':
            Transaction transactionB = new Transaction(op.getTransactionId(), op.getTimeStamp(), op.isReadOnly());
            transactionMap.put(op.getTransactionId(), transactionB);
            transactionList.add(op.getTransactionId());
            if (op.isReadOnly()) {
                Map<Integer, Integer> snapshot = dm.getSnapshot();
                for (Integer var : snapshot.keySet()) {
                    transactionB.putSnapshotVal(var.intValue(), snapshot.get(var.intValue()).intValue());
                }
            } else {
                dm.beginTransactionRW(op.getTransactionId());
            }
            deadlock.removeVertex(op.getTransactionId());
            break;
        case 'E':
            Transaction transactionE = transactionMap.get(op.getTransactionId());
            if (transactionE.isReadOnly()) {
                output.append(op.getTransactionId() + " commits\n");
            } else {
                if (transactionMap.get(op.getTransactionId()).isMarkedForAbort()) {
                    abortTransaction(op.getTransactionId(), "Site failure");
                } else if (dm.canCommit(op.getTransactionId(), transactionE.getBeginTime())) {
                    dm.commit(op.getTransactionId(), transactionE.getModifiedVals());
                    output.append(op.getTransactionId() + " commits\n");
                } else {
                    abortTransaction(op.getTransactionId(), "Site failure");
                }
            }
            transactionMap.remove(op.getTransactionId());
            transactionList.remove(op.getTransactionId());
            deadlock.removeVertex(op.getTransactionId());
            break;
        case 'F':
            List<String> transactionsToAbort = dm.failAndGetAffectedTransactionIds(op.getSiteId(), op.getTimeStamp());
            for (String transactionToAbort : transactionsToAbort) {
                transactionMap.get(transactionToAbort).markForAbort();
            }
            break;
        case 'H':
            dm.recover(op.getSiteId(), op.getTimeStamp());
            break;
        case 'R':
            Transaction currTransaction = transactionMap.get(op.getTransactionId());
            if (currTransaction.isReadOnly()) {
                if (dm.anySiteUpForVar(op.getVar())) {
                    output.append("x" + op.getVar() + ": " + currTransaction.getSnapshotVal(op.getVar()) + "\n");
                    if (readFromQ) {
                        operationQ.remove(indexInQ.intValue());
                    }
                } else {
                    if (readFromQ) {
                        indexInQ++;
                    }
                    if (!operationQ.contains(op)) {
                        operationQ.add(op);
                    }
                }

            } else {
                // check if a write is in the queue, if so reply no
                boolean isWriteInQ = false;
                List<String> guiltyTransactionIdsWrite = new ArrayList<String>();
                for (Operation opToCheck : operationQ) {
                    if (opToCheck.getVar() == op.getVar() && opToCheck.getType() == 'W') {
                        isWriteInQ = true;
                        guiltyTransactionIdsWrite.add(opToCheck.getTransactionId());
                    }
                }
                ReadLockResponse readResponse;
                if (isWriteInQ) {
                    readResponse = new ReadLockResponse(false, false, 0, guiltyTransactionIdsWrite);
                } else {
                    readResponse = dm.readVal(op.getVar(), op.getTransactionId());
                }
                if (readResponse.isGranted()) {
                    int ans = readResponse.getVal();
                    if (currTransaction.hasModifiedVal(op.getVar())) {
                        ans = currTransaction.getModifiedVal(op.getVar());
                    }
                    output.append("x" + Integer.toString(op.getVar()) + ": " + ans + "\n");
                    if (readFromQ) {
                        operationQ.remove(indexInQ.intValue());
                    }
                } else {
                    result = false;
                    if (readFromQ) {
                        indexInQ++;
                    }
                    if (!readResponse.isUnsafe()) {
                        if (!operationQ.contains(op)) {
                            operationQ.add(op);
                            for (String guiltyTransactionId : readResponse.getGuiltyTransactionIds()) {
                                if (op.getTransactionId().equals(guiltyTransactionId)) {
                                    continue;
                                }
                                deadlock.addEdge(op.getTransactionId(), guiltyTransactionId);
                            }
                        }
                    } else {
                        if (!operationQ.contains(op)) {
                            operationQ.add(op);
                        }
                    }
                }
            }
            break;
        case 'W':
            WriteLockResponse writeResponse = dm.writeVal(op.getVar(), op.getTransactionId(), op.getVal());
            if (writeResponse.isGranted()) {
                transactionMap.get(op.getTransactionId()).putModifiedVal(op.getVar(), op.getVal());
                if (readFromQ) {
                    operationQ.remove(indexInQ.intValue());
                }
            } else {
                if (readFromQ) {
                    indexInQ++;
                }
                result = false;
                if (!writeResponse.isUnsafe()) {
                    if (!operationQ.contains(op)) {
                        operationQ.add(op);
                        for (String guiltyTransactionId : writeResponse.getGuiltyTransactionIds()) {
                            if (op.getTransactionId().equals(guiltyTransactionId)) {
                                continue;
                            }
                            deadlock.addEdge(op.getTransactionId(), guiltyTransactionId);
                        }
                    }
                } else {
                    if (!operationQ.contains(op)) {
                        operationQ.add(op);
                    }
                }
            }
            break;
        default:
            break;
        }

        // if this op is E or H, read from beginning of queue
        if (op.getType() == 'E' || op.getType() == 'H') {
            readFromQ = true;
            indexInQ = 0;
        }
        return result;

    }

    // get next operation either from queue or from file
    Operation getNextOperation() {
        if (breakCycle()) {
            readFromQ = true;
            indexInQ = 0;
        }

        // read from Q if applicable, otherwise call readNextEvent
        if (readFromQ) {
            if (indexInQ >= operationQ.size()) {
                readFromQ = false;
                indexInQ = null;
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
        if (line == null || line.trim().equals(""))
            return null;
        String[] op = line.split("[()]");
        for (int i = 0; i < op.length; i++) {
            op[i] = op[i].replaceAll("\\s+", "");
        }
        if (op.length < 1)
            return null;
        if (op[0].equals("begin")) {
            return new OperationBE('B', tick++, op[1], false);
        } else if (op[0].equals("beginRO")) {
            return new OperationBE('B', tick++, op[1], true);
        } else if (op[0].equals("end")) {
            return new OperationBE('E', tick++, op[1], false);
        } else if (op[0].equals("R")) {
            String[] splits = op[1].split(",");
            return new OperationRW('R', tick++, splits[0], Integer.parseInt(splits[1].substring(1)), 0);
        } else if (op[0].equals("W")) {
            String[] splits = op[1].split(",");
            return new OperationRW('W', tick++, splits[0], Integer.parseInt(splits[1].substring(1)),
                    Integer.parseInt(splits[2]));
        } else if (op[0].equals("fail")) {
            return new OperationFH('F', tick++, Integer.parseInt(op[1]));
        } else if (op[0].equals("recover")) {
            return new OperationFH('H', tick++, Integer.parseInt(op[1]));
        } else if (op[0].equals("dump")) {
            String dumpOutput = dm.dumpValues();
            output.append(dumpOutput + "\n");
            tick++;
            return readNextEvent();
        }
        return null;
    }

    // get next line from file
    String readNextLine() {
        String line = null;
        try {
            line = this.reader.readLine();
        } catch (Exception e) {
            System.out.println("Exception thrown:" + e);
            System.exit(0);
        }
        return line;
    }

    // this will be called from main in a loop
    boolean processNextOperation() {
        Operation op = getNextOperation();
        if (op == null) {
            System.out.println(output.toString().trim());
            return false;
        }
        process(op);
        return true;
    }
}