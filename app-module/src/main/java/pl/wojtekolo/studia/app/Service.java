package pl.wojtekolo.studia.app;

import processing.Processor;
import processing.StatusListener;

import java.util.ArrayList;
import java.util.List;

public class Service {
    public boolean submit(List<String> paths, List<String> columns, ProcessorInfoDto processor, StatusListener listener){

        StringBuilder task = new StringBuilder();

        for (int i = 0; i < paths.size()-1; i++){
            task.append(paths.get(i)).append(",");
        }
        task.append(paths.getLast()).append(";");

        for (int i = 0; i < columns.size()-1; i++){
            task.append(columns.get(i)).append(",");
        }
        task.append(paths.getLast());

        return processor.processor().submitTask(task.toString(), listener);
    }

    public List<ProcessorInfoDto> getProcessors(){
        List<ProcessorInfoDto> result = new ArrayList<>();
        Processor processor1 = loadProcessor("eee");
        Processor processor2 = loadProcessor("aaa");
        result.add(new ProcessorInfoDto(processor1, "eee", processor1.getInfo()));
        result.add(new ProcessorInfoDto(processor2, "aaa", processor2.getInfo()));
        return result;
    }

    public Processor loadProcessor(String classPath){
        return new ExampleProcessor();
    }
}
