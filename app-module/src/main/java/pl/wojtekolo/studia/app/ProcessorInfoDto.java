package pl.wojtekolo.studia.app;

import processing.Processor;

public record ProcessorInfoDto(
        Processor processor,
        String name,
        String info
) {
    @Override
    public String toString() {
        return "Nazwa: " + name + ", info: " + info;
    }
}
