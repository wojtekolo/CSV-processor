package pl.wojtekolo.studia.app;

import processing.Processor;

public record ProcessorInfoDto(
        Processor processor,
        String info
) {
    @Override
    public String toString() {
        return "Typ operacji:  " + info;
    }
}
