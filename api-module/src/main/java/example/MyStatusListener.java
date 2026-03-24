package example;

import processing.Status;
import processing.StatusListener;

public class MyStatusListener implements StatusListener {
    // listener jest znany podczas kompilacji projektu
    // można więc wiązać się w nim z interfejsem użytkownika
    @Override
    public void statusChanged(Status s) {
        System.out.println("Progress:"+s.getProgress()+" TaskId:" +s.getTaskId());
    }

}
