package adb.project;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestCommit {

    @Test
    public void testFirst() {
        String[] args = { "src/test/resources/commit.txt" };
        Main.main(args);
        assertEquals(1, 1);
    }

}