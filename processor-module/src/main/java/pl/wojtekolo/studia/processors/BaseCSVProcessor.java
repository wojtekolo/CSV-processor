package pl.wojtekolo.studia.processors;

import processing.Processor;
import processing.StatusListener;

import java.io.*;

public class BaseCSVProcessor implements Processor {
    @Override
    public boolean submitTask(String task, StatusListener sl) {
        return false;
    }

    @Override
    public String getInfo() {
        return "Base processor for joining csv files";
    }

    @Override
    public String getResult() {
        return "";
    }

    private void joinCsv(File file1, File file2, String resultFileName){
        try {
            BufferedReader reader1 = new BufferedReader(new FileReader(file1));
            BufferedReader reader2 = new BufferedReader(new FileReader(file2));
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(resultFileName)));

            String line1 = reader1.readLine();
            String line2 = reader2.readLine();

            String []columns1 = line1.split(";");
            String []columns2 = line2.split(";");

            writer.append(line1).append(";").append(line2).append("\n");
            writer.flush();

            boolean finished = false;
            while (!finished){
                System.out.println(columns2.length);
                finished = combineLines(reader1, columns1.length, reader2, columns2.length, writer);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean combineLines(BufferedReader reader1, int columns1, BufferedReader reader2, int columns2,  BufferedWriter writer){
        boolean finish = true;
        String line1;
        try {
            if ((line1 = reader1.readLine())==null){
                line1 = ";".repeat(columns1-1);
            } else finish = false;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String line2;
        try {
            if ((line2 = reader2.readLine())==null){
                line2 = ";".repeat(columns2-1);
            } else finish = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (finish) return finish;

        try {
            writer.append(line1).append(";").append(line2).append("\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return finish;
    }

    static void main() {
        BaseCSVProcessor processor = new BaseCSVProcessor();
        processor.joinCsv(new File("myfile1"), new File("myfile2"), "result");
    }
}
