package adb.project;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestDeadlockAbort {

    @Test
    public void testFirst() {
        String[] args = { "self", "src/test/resources/deadlockAbort.txt" };
        Main.main(args);
    }
}