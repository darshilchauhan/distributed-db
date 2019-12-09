package adb.project;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestReadOnly {

    @Test
    public void testFirst() {
        String[] args = { "src/test/resources/readonly.txt" };
        Main.main(args);
    }

}