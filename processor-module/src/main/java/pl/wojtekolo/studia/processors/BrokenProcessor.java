package pl.wojtekolo.studia.processors;

import processing.StatusListener;

public class BrokenProcessor extends BaseCSVProcessor{

    @Override
    public boolean submitTask(String task, StatusListener sl) {
        return false;
    }

    @Override
    public String getInfo() {
        return "Zwraca błąd przy próbie użycia";
    }
}
