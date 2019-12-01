package adb.project;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestSingleRead {
    String fileName = System.getProperty("fileName");

    @Test
    public void testLucky() {
        assertEquals(7, 7);
        // assertEquals(7, 8);
        assertEquals(fileName, "blah");
    }

}