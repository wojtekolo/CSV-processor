package pl.wojtekolo.studia.processors;

import processing.StatusListener;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThirdLineCSVProcessor extends BaseCSVProcessor{
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private String result = null;

    @Override
    public boolean submitTask(String task, StatusListener sl) {
        executor.submit(()->{
            BaseCSVProcessor.increaseTotalTasks();
            taskId = BaseCSVProcessor.getTotalTasks();
            super.submitTask(task,sl);
            try {
                leaveOneOfEveryThreeRows();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            simulateProgress(taskId,0,100,100, sl);
            result = new File(getPath("final-three")).getAbsolutePath();
        });
        return true;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public String getInfo() {
        return "Zostawia tylko co trzeci wiersz";
    }

    private void leaveOneOfEveryThreeRows() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(getPath("final-full")));
             BufferedWriter writer = new BufferedWriter(new FileWriter(getPath("final-three")))) {

            String line = reader.readLine();
            writer.append(line).append("\n");

            int count = 3;
            while ((line = reader.readLine()) != null){
                if (count==3){
                    writer.append(line).append("\n");
                    count=1;
                }else {
                    count++;
                }
            }
        }
    }
}
