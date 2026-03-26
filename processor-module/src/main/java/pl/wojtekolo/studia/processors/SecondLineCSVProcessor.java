package pl.wojtekolo.studia.processors;

import processing.StatusListener;

import java.io.*;

public class SecondLineCSVProcessor extends BaseCSVProcessor {

    @Override
    public boolean submitTask(String task, StatusListener sl) {
        int taskId = BaseCSVProcessor.getTotalTasks();
        BaseCSVProcessor.increaseTotalTasks();
        super.submitTask(task,sl);
        try {
            leaveOneOfEveryTwoRows();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        simulateProgress(taskId,0,100,100, sl);
        return true;
    }

    @Override
    public String getResult() {
        return new File("final-two").getAbsolutePath();
    }

    @Override
    public String getInfo() {
        return "Zostawia tylko co drugi wiersz";
    }

    private void leaveOneOfEveryTwoRows() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("final-full"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("final-two"));

        String line = reader.readLine();
        writer.append(line).append("\n");

        boolean write = true;
        while ((line = reader.readLine()) != null){
            if (write){
                writer.append(line).append("\n");
                write = false;
            }else {
                write = true;
            }
        }

        writer.flush();
        writer.close();
        reader.close();
    }
}


