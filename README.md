# distributed-db

Distributed database with multiversion concurrency control, deadlock detection, replication and failure recovery.

Features:
multiversion concurrency control,
deadlock detection,
replication,
failure recovery

---

TM - Transaction manager
Transaction - class for holding transaction info. Does not act on them.
DM - Data manager, middle man between TM and site. Basically Site manager.
Site - just holds info about site and updates it.

Site:

- final int siteID (between 1 to 10 inclusive)
- Set<Integer> designatedVars
- bool isUp
- Map<Integer, Integer> commitedVals: commited values of variables. Size 20.
- Set<Integer> safeVars: all designated vars are included in the beginning. After fail and recovery, only given ones are safe.
- HashMap<Integer, Set<String>> readLockTable: this variable is read-locked by these transactions (IDs) (set because transaction can be many)
- HashMap<Integer, String> writeLockTable: this variable is write-locked by this transaction (ID)
- HashMap<String, List<Integer>> readLockInfo: this transaction has readlock on these vars
- HashMap<String, List<Integer>> writeLockInfo: this transaction has writelock on these vars

- constructor Site(int siteID)
- bool getReadLock(int var, String transaction): If it is read or write-locked by same transaction, then return yes. If it is write-locked by some other transaction, return no. If it is read-locked by some other transaction, add this transaction to read-lock list and return yes.
- int readVal(int var): return value from commitedValues
- int writeVal(int var, int val): commitedValues[var-1]=val. Make this variable safe if it is not.
- void releaseReadLock(int var, String transaction): release the lock for this var by this transaction. Update both locktable and lockInfo.
- void releaseWriteLock(int var, String transaction): same as above.
- void releaseReadLocks(String transaction): go through readLockInfo for this transaction and store all locked variables in a new list. Call releaseReadLock(var, transaction) for each lock.
- void releaseWriteLocks(String transaction): same as above.
- void clearLocks(): clear all locks from both locktable and lockinfo.
- void fail(): isUp=false. clearLocks().
- void recover(List<Integer> safeVars): isUp=true, only these variables are safe, rest are unsafe.

DM:

- final HashMap<Integer, List<Integer>> variableLocations: this variable is at these sites (IDs)
- List<Site>: 10 site objects. Make it arraylist.

- constructor DataManager(): give value to variablelocations, create 10 site objects with id 1 to 10.
- bool getReadLock(int var, String transaction): Go through available locations for the variable. Check site is up. call getReadLock on site. If any come back true, return true. If all false, return false.
- bool getWriteLock(int var, String transaction): same as above.
- void releaseLocks(String transaction): called when transaction commits/aborts. Call releaseReadLocks and releaseWriteLocks for this transaction for each site.
- void fail(int siteID): tell this site to fail.
- void recover(int siteID): tell this site to recover. Need to send which variables are safe. If any other site is up, then even vars are not safe. For odd vars, check if designated site is up, safe if the site is not up. Then include it in safeVars.

TM:

- DataManager DM
- HashMap<String, Transaction> transactions: this transactionID belongs to this transaction object.
- List<String> transactionList: list of IDs. In order of first come first served. For Deadlock.
- HashMap<Integer, Queue<Operation>> operationQ: this variable has this queue of operations waiting to be executed
- DeadLockMap: for detecting deadlocks.
- String inputFileString
- File inputFile

- constructor TM(String inputFileString): initialize transactions as empty. Initialize transactionList as empty. Initialize operationQ with 20 variables and empty queue of operations. Initialize deadlockMap. Int
- process(Operation op): F: call DM.fail(siteID). R: call DM.recover(siteID). For R and W try to execute the operation, if not possible, add it to the queue. E: releaseLocks(transaction)
- Operation readNextEvent(): read next line and return if valid operation. otherwise call fail/recover/end(Ti)

Transaction

- final int beginTime: the incremental timestamp when begin(Tx) happened.
- final String transactionID
- final String type: "RW" or "RO"

- constructor TransactionRW(String ID, int beginTime)
- getID(), getBeginTime(), getType

TransactionRW extends class Transaction type="RW"
TransactionRO extends class Transaction type="RO"

Operation:

- final int timeStamp
- final char type
- final char type: 'R', 'W', 'F' fail, 'C' recovery, 'E' end
- final String transaction ID: empty if 'F' or 'C'
- final int var: which variable to read or write, 0 if not 'R' or 'W'
- final int val: Actual value if write. 0 for anything else.
- methods to get all above

DeadLockMap:

- List<List<String>> graph: transactionIDs are node. Directed edge from T1 to T2 if T1 is waiting for T2.
- List<String> findCycle(): returns empty list if no cycle is found. Otherwise return list of transactions in a cycle(s)
