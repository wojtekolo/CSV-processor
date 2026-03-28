package pl.wojtekolo.studia.processors;

import processing.Processor;
import processing.Status;
import processing.StatusListener;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public abstract class BaseCSVProcessor implements Processor {
    private static int totalTasks;
    protected int taskId;
    protected Path rootPath;

    @Override
    public boolean submitTask(String task, StatusListener sl) {
        rootPath = Path.of("data", "task_" + taskId);
        try {
            Files.createDirectories(rootPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String[] pathsAndColumns = task.split(";");
        String[] paths = pathsAndColumns[0].split(",");
        String[] columns = pathsAndColumns[1].split(",");

        if (paths.length < 2) throw new RuntimeException("Podano mniej niż 2 pliki");
        joinCsv(paths[0], paths[1], getPath("tmp1"));

        for (int i = 2; i < paths.length; i++) {
            joinCsv(getPath("tmp" + (i - 1)), paths[i], getPath("tmp" + i));
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(getPath("tmp" + (paths.length - 1))))){
            String header = reader.readLine();
            removeColumns(
                    getPath("tmp" + (paths.length - 1)),
                    getPath("final-full"),
                    getColumnToLeave(columns, header)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 1; i < paths.length; i++) {
            Path path = rootPath.resolve("tmp" +i);
            try {
                Files.delete(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    @Override
    public String getResult() {
        return null;
    }

    private void joinCsv(String filePath1, String filePath2, String resultFilePath) {
        try (BufferedReader reader1 = new BufferedReader(new FileReader(filePath1));
             BufferedReader reader2 = new BufferedReader(new FileReader(filePath2));
             BufferedWriter writer = new BufferedWriter(new FileWriter(resultFilePath))) {

            String line1 = reader1.readLine();
            String line2 = reader2.readLine();

            String[] columns1 = line1.split(";");
            String[] columns2 = line2.split(";");

            writer.append(line1).append(";").append(line2).append("\n");
            writer.flush();

            boolean finished = false;
            while (!finished) {
                finished = combineLines(reader1, columns1.length, reader2, columns2.length, writer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean combineLines(BufferedReader reader1, int columns1, BufferedReader reader2, int columns2, BufferedWriter writer) {
        boolean finish = true;
        String line1;
        try {
            if ((line1 = reader1.readLine()) == null) {
                line1 = ";".repeat(columns1 - 1);
            } else finish = false;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String line2;
        try {
            if ((line2 = reader2.readLine()) == null) {
                line2 = ";".repeat(columns2 - 1);
            } else finish = false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (finish) return true;

        try {
            writer.append(line1).append(";").append(line2).append("\n");
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private void removeColumns(String originalPath, String resultPath, boolean[] columnsToLeave) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(originalPath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(resultPath))){

            String line;
            line = reader.readLine();
            int totalColumnCount = line.split(";").length;

            while (line != null) {
                StringBuilder newLine = new StringBuilder();
                String[] columns = line.split(";", -1);

                for (int i = 0; i < totalColumnCount; i++) {
                    if (columnsToLeave[i]) newLine.append(columns[i]).append(";");
                }

                newLine.deleteCharAt(newLine.length() - 1);
                writer.append(newLine).append("\n");
                writer.flush();

                line = reader.readLine();
            }

        }
    }

    private boolean[] getColumnToLeave(String[] columnNamesToLeave, String fileHeader) {
        String[] columns = fileHeader.split(";", -1);
        boolean[] columnsToLeave = new boolean[columns.length];

        for (int i = 0; i < columns.length; i++) {
            for (var columnToLeave : columnNamesToLeave) {
                if (Objects.equals(columns[i], columnToLeave)) {
                    columnsToLeave[i] = true;
                    break;
                }
            }
        }
        return columnsToLeave;
    }

    protected void simulateProgress(int taskId, int currentProgress, int progressToMake, int delayMs, StatusListener sl) {
        if (sl == null) return;

        int targetProgress = Math.min(currentProgress + progressToMake, 100);
        int progress = currentProgress;
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        while (progress < targetProgress) {
            progress += rand.nextInt(1, 6);
            if (progress > targetProgress) progress = targetProgress;

            sl.statusChanged(new Status(taskId, progress));

            long delay = (long) (delayMs * rand.nextDouble(0.8, 1.2));

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    protected static int getTotalTasks() {
        return totalTasks;
    }

    protected static void increaseTotalTasks() {
        totalTasks++;
    }

    protected String getPath(String file){
        return String.valueOf(rootPath.resolve(file));
    }
}
