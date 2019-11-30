class Transaction {
    String id;
    int beginTime;

    Transaction(String id, int beginTime) {
        this.id = id;
        this.beginTime = beginTime;
    }

    String getID() {
        return this.id;
    }

    int getBeginTime() {
        return this.beginTime;
    }
}