package adb.project;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FileReader;

import org.junit.Test;

public class TestSample {
    String samplesFolder = "src/test/resources/samples/";

    String getFileContent(String path) {
        StringBuilder fileOutput = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine() + "\n";
            while (line != null && !line.equals("")) {
                fileOutput.append(line.trim() + "\n");
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            System.out.println("Error reading file " + path + ": " + e.getMessage());
        }
        return fileOutput.toString().trim();
    }

    @Test
    public void testFirst() {
        TransactionManager manager = new TransactionManager(samplesFolder + "test1.txt");
        while (manager.processNextOperation()) {
        }
        // System.out.println("TM output:\n" + manager.output.toString().trim());
        // System.out.println("\n\n\nFile output:\n" + getFileContent(samplesFolder +
        // "out1.txt").trim());
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out1.txt").trim());
    }

}