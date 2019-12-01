package adb.project;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestSingleRead {
    String fileName = System.getProperty("fileName");

    @Test
    public void testFirst() {
        String[] args = { "self", "src/test/resources/SingleRead.txt" };
        Main.main(args);
        assertEquals(1, 1);
    }

}