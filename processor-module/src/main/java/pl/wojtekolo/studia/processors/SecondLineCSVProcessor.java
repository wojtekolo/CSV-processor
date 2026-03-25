package pl.wojtekolo.studia.processors;

import processing.StatusListener;

import java.io.*;

public class SecondLineCSVProcessor extends BaseCSVProcessor {

    @Override
    public boolean submitTask(String task, StatusListener sl) {
        super.submitTask(task,sl);
        try {
            leaveOneOfEveryTwoRows();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public String getResult() {
        return new File("final-two").getAbsolutePath();
    }

    private void leaveOneOfEveryTwoRows() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("result"));
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


