package adb.project;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestFail {

    @Test
    public void testFirst() {
        String[] args = { "src/test/resources/fail.txt" };
        Main.main(args);
    }

}