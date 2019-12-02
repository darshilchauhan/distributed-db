package adb.project;

import static org.junit.Assert.assertEquals;
import java.util.*;
import org.junit.Test;

public class TestDeadlock {

    @Test
    public void testFirst() {
        Deadlock dl = new Deadlock();
        dl.addEdge("T1", "T2");
        dl.addEdge("T2", "T1");
        List<String> cycle = dl.findCycle();
        List<String> truth = new ArrayList<>(List.of("T1", "T2"));
        assertEquals(cycle, truth);
    }

    @Test
    public void testSecond() {
        Deadlock dl = new Deadlock();
        dl.addEdge("M1", "M2");
        dl.addEdge("M2", "M3");
        List<String> cycle = dl.findCycle();
        List<String> truth = new ArrayList<>();
        assertEquals(cycle, truth);
    }
}