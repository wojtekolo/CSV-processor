package pl.wojtekolo.studia.app;

import processing.Processor;
import processing.StatusListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service {
    private final Map<Integer, Class<?>> processorClasses = new HashMap<>();

    public Service(MyClassLoader classLoader) {
        try {
            processorClasses.put(1, classLoader.loadClass("pl.wojtekolo.studia.processors.SecondLineCSVProcessor"));
            processorClasses.put(2, classLoader.loadClass("pl.wojtekolo.studia.processors.ThirdLineCSVProcessor"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean submit(List<String> paths, List<String> columns, ProcessorInfoDto processorInfo, StatusListener listener) {

        StringBuilder task = new StringBuilder();

        for (int i = 0; i < paths.size()-1; i++){
            task.append(paths.get(i)).append(",");
        }
        task.append(paths.getLast()).append(";");

        for (int i = 0; i < columns.size()-1; i++){
            task.append(columns.get(i)).append(",");
        }
        task.append(columns.getLast());

        try {
            Class<?> processorClass = processorClasses.get(processorInfo.id());
            Constructor<?> constructor = processorClass.getConstructor();
            Object processorObject = constructor.newInstance();

            Method submitMethod = processorClass.getDeclaredMethod("submitTask", String.class, StatusListener.class);

            Object result = submitMethod.invoke(processorObject, task.toString(), listener);

            if (result instanceof Boolean b){
                return b;
            } else {
                return false;
            }
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProcessorInfoDto> getProcessors() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        List<ProcessorInfoDto> result = new ArrayList<>();

        for (Map.Entry<Integer, Class<?>> entry : processorClasses.entrySet()){
            Method infoMethod = entry.getValue().getDeclaredMethod("getInfo");
            Object processor = entry.getValue().getConstructor().newInstance();
            Object methodResult = infoMethod.invoke(processor);
            if (methodResult instanceof String info){
                result.add(new ProcessorInfoDto(entry.getKey(), info));
            } else {
                throw new RuntimeException("Processor didn't return String when getInfo() was called");
            }
        }

        return result;
    }

    public Processor loadProcessor(String classPath){
        return new ExampleProcessor();
    }
}
