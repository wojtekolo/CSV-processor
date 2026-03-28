package pl.wojtekolo.studia.processors;

import processing.StatusListener;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SecondLineCSVProcessor extends BaseCSVProcessor {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String result = null;

    @Override
    public boolean submitTask(String task, StatusListener sl) {

        executor.submit(()->{
            BaseCSVProcessor.increaseTotalTasks();
            taskId = BaseCSVProcessor.getTotalTasks();
            super.submitTask(task,sl);
            try {
                leaveOneOfEveryTwoRows();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            simulateProgress(taskId,0,100,100, sl);
            result = new File(getPath("final-two")).getAbsolutePath();
        });

        return true;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public String getInfo() {
        return "Zostawia tylko co drugi wiersz";
    }

    private void leaveOneOfEveryTwoRows() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(getPath("final-full")));
             BufferedWriter writer = new BufferedWriter(new FileWriter(getPath("final-two")))){
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
        }
    }
}


