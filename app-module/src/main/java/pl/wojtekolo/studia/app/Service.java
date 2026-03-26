package pl.wojtekolo.studia.app;

import processing.Processor;
import processing.StatusListener;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class Service {
    private final MyClassLoader classLoader;

    public Service(MyClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public boolean submit(List<String> paths, List<String> columns, ProcessorInfoDto processor, StatusListener listener){

        StringBuilder task = new StringBuilder();

        for (int i = 0; i < paths.size()-1; i++){
            task.append(paths.get(i)).append(",");
        }
        task.append(paths.getLast()).append(";");

        for (int i = 0; i < columns.size()-1; i++){
            task.append(columns.get(i)).append(",");
        }
        task.append(columns.getLast());

        return processor.processor().submitTask(task.toString(), listener);
    }

    public List<ProcessorInfoDto> getProcessors() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<ProcessorInfoDto> result = new ArrayList<>();
        Processor processor1 = (Processor) classLoader.loadClass("pl.wojtekolo.studia.processors.SecondLineCSVProcessor").getConstructor().newInstance();
        Processor processor2 = (Processor) classLoader.loadClass("pl.wojtekolo.studia.processors.ThirdLineCSVProcessor").getConstructor().newInstance();
        result.add(new ProcessorInfoDto(processor1, processor1.getInfo()));
        result.add(new ProcessorInfoDto(processor2, processor2.getInfo()));
        return result;
    }

    public Processor loadProcessor(String classPath){
        return new ExampleProcessor();
    }
}
