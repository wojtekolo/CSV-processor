package pl.wojtekolo.studia.processors;

import processing.StatusListener;

import java.io.*;

public class ThirdLineCSVProcessor extends BaseCSVProcessor{

    @Override
    public boolean submitTask(String task, StatusListener sl) {
        int taskId = BaseCSVProcessor.getTotalTasks();
        BaseCSVProcessor.increaseTotalTasks();
        super.submitTask(task,sl);
        try {
            leaveOneOfEveryThreeRows();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        simulateProgress(taskId,0,100,100, sl);
        return true;
    }

    @Override
    public String getResult() {
        return new File("final-three").getAbsolutePath();
    }

    private void leaveOneOfEveryThreeRows() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("final-full"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("final-three"));

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

        writer.flush();
        writer.close();
        reader.close();
    }
}
