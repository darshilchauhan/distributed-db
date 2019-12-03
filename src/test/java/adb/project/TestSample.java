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

    // @Test
    // public void test1() {
    // System.out.println("Test1 Output:");
    // TransactionManager manager = new TransactionManager(samplesFolder +
    // "test1.txt");
    // while (manager.processNextOperation()) {
    // }
    // assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder +
    // "out1.txt").trim());
    // }

    // @Test
    // public void test2() {
    // System.out.println("\nTest2 Output:");
    // TransactionManager manager = new TransactionManager(samplesFolder +
    // "test2.txt");
    // while (manager.processNextOperation()) {
    // }
    // assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder +
    // "out2.txt").trim());
    // }

    // @Test
    // public void test3() {
    // System.out.println("\nTest3 Output:");
    // TransactionManager manager = new TransactionManager(samplesFolder +
    // "test3.txt");
    // while (manager.processNextOperation()) {
    // }
    // assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder +
    // "out3.txt").trim());
    // }

    @Test
    public void test3_5() {
        System.out.println("\nTest3_5 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test3_5.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out3_5.txt").trim());
    }

    @Test
    public void test3_7() {
        System.out.println("\nTest3_7 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test3_7.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out3_7.txt").trim());
    }

}