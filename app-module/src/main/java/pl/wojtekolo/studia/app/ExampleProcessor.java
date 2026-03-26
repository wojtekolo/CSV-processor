package pl.wojtekolo.studia.app;

import processing.Processor;
import processing.Status;
import processing.StatusListener;

import java.util.concurrent.ThreadLocalRandom;

public class ExampleProcessor implements Processor {
    @Override
    public boolean submitTask(String task, StatusListener sl) {
        simulateProgress(0, 0, 100, 100, sl);
        return true;
    }

    @Override
    public String getInfo() {
        return "Example processor 1";
    }

    @Override
    public String getResult() {
        return "result example";
    }
    protected void simulateProgress(int taskId, int currentProgress, int progressToMake, int delayMs, StatusListener sl) {
        if (sl == null) return;

        int targetProgress = Math.min(currentProgress + progressToMake, 100);
        int progress = currentProgress;
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        while (progress < targetProgress) {
            System.out.println("progress "+progress);
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
}
