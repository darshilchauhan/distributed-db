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
    public void test1() {
        System.out.println("Test1 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test1.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out1.txt").trim());
    }

    @Test
    public void test2() {
        System.out.println("\nTest2 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test2.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out2.txt").trim());
    }

    @Test
    public void test3() {
        System.out.println("\nTest3 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test3.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out3.txt").trim());
    }

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

    @Test
    public void test4() {
        System.out.println("\nTest4 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test4.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out4.txt").trim());
    }

    @Test
    public void test5() {
        System.out.println("\nTest5 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test5.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out5.txt").trim());
    }

    @Test
    public void test6() {
        System.out.println("\nTest6 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test6.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out6.txt").trim());
    }

    @Test
    public void test6_2() {
        System.out.println("\nTest6_2 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test6_2.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out6_2.txt").trim());
    }

    @Test
    public void test7() {
        System.out.println("\nTest7 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test7.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out7.txt").trim());
    }

    @Test
    public void test8() {
        System.out.println("\nTest8 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test8.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out8.txt").trim());
    }

    @Test
    public void test9() {
        System.out.println("\nTest9 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test9.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out9.txt").trim());
    }

    @Test
    public void test10() {
        System.out.println("\nTest10 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test10.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out10.txt").trim());
    }

    @Test
    public void test11() {
        System.out.println("\nTest11 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test11.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out11.txt").trim());
    }

    @Test
    public void test12() {
        System.out.println("\nTest12 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test12.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out12.txt").trim());
    }

    @Test
    public void test13() {
        System.out.println("\nTest13 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test13.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out13.txt").trim());
    }

    @Test
    public void test14() {
        System.out.println("\nTest14 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test14.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out14.txt").trim());
    }

    @Test
    public void test15() {
        System.out.println("\nTest15 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test15.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out15.txt").trim());
    }

    @Test
    public void test16() {
        System.out.println("\nTest16 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test16.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out16.txt").trim());
    }

    @Test
    public void test17() {
        System.out.println("\nTest17 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test17.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out17.txt").trim());
    }

    @Test
    public void test18() {
        System.out.println("\nTest18 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test18.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out18.txt").trim());
    }

    @Test
    public void test19() {
        System.out.println("\nTest19 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test19.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out19.txt").trim());
    }

    @Test
    public void test20() {
        System.out.println("\nTest20 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test20.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out20.txt").trim());
    }

    @Test
    public void test20_2() {
        System.out.println("\nTest20_2 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test20_2.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out20_2.txt").trim());
    }

    @Test
    public void test21() {
        System.out.println("\nTest21 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test21.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out21.txt").trim());
    }

    @Test
    public void test22() {
        // readonly should wait if no site up
        System.out.println("\nTest22 Output:");
        TransactionManager manager = new TransactionManager(samplesFolder + "test22.txt");
        while (manager.processNextOperation()) {
        }
        assertEquals(manager.output.toString().trim(), getFileContent(samplesFolder + "out22.txt").trim());
    }

}