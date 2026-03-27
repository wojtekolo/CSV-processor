package pl.wojtekolo.studia.app;

import processing.Processor;

public record ProcessorInfoDto(
        int id,
        String info
) {
    @Override
    public String toString() {
        return "Typ operacji:  " + info;
    }
}
