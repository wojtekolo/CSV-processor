package example;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import processing.StatusListener;

public class Main {

    public static void main(String[] args) {
        // Niech zmienna p reprezentuje referencję do jakiejś nieznanej klasy,
        // implementującej interfejs Processor, załadowanej własnym ładowaczem.
        // Jednak w tym przykładowym kodzie nie dostarczono żadnego takiego ładowacza klas.
        // Zamiast tego do p przypisano wartość wyłuskana bezpośrednio z klasy MyProcessor
        // (implementującej interfejs Processor, ale znanej na etapie kompilacji projektu).
        // W ten sposób "zasymulowano" ładowanie klasy.
        Class<?> p = MyProcessor.class;
        // Mając załadowaną klasę można sięgnąć do jej metod i je wywołać.
        try {
            // Pozyskujemy konstruktor
            Constructor<?> cp = p.getConstructor();
            // a następnie tworzymy obiekt załadowanej klasy
            // (trzeba to zrobić, aby można było wywołać metody instancyjne).
            Object o = cp.newInstance();



            // Ponieważ wiemy, że załadowana klasa implementuje interfejs processing.Processor,
            // dlatego szukamy w tej klasie znanych metod.
            // Najpierw dowiadujemy się, na czym polega algorytm przetwarzania wywołując getInfo()
            Method getInfoMethod = p.getDeclaredMethod("getInfo");
            System.out.println((String)getInfoMethod.invoke(o));

            // Następnie przesyłamy zadanie do wykonania metodą submitTask()
            // Method method = p.getDeclaredMethod("submitTask", new Class[] {String.class,
            // StatusListener.class});
            Method submitTaskMethod = p.getDeclaredMethod("submitTask", String.class, StatusListener.class);

            // Aby wywołać tę metodę należy posłużyć się odpowiednimi parametrami: task, sl
            // gdzie sl referencja do własnego słuchacza - instancji klasy implementującej interfejs StatusListener.

            // boolean b = (boolean) method.invoke(o,new Object[] {"Text to process", new
            // MyStatusListener()});
            boolean b = (boolean) submitTaskMethod.invoke(o, "Tekst na wejście", new MyStatusListener());

            // Implementacja metody submitTask w klasie MyProcessor jest asynchroniczna.
            // Do rozpoznania końca przetwarzania i pobrania wyliczonego wyniku
            // należy wykorzystać metody słuchacza.
            // Na razie piszemy tylko komunikat, że zainicjowano przetwarzanie
            if (b)
                System.out.println("Processing started correctly");
            else
                System.out.println("Processing ended with an error");

            // Do rozpoznania końca przetwarzania służy metoda statusChanged
            // słuchacza MyStatusListener. Do tej metody dostarczana będzie instancja Status,
            // z której wyciągnąć można progress i taskId.
            // Jak progress dojdzie do końca, będzie można pobrać wynik przetwarzania
            // getodą getResult() z instancji klasy MyProcessor.
            // Jeśli z metody getResult() otrzyma się coś innego niż null, to właśnie będzie to
            // wynik

            // Ponieważ mamy program konsolowy, asynchroniczność wymaga uruchamieniam wątku
            // oczekującego na rezultat (sprawdzający, czy przetwarzanie nie dobiegło końca).
            // W aplikacji okienkowej podobnie zadziałałby słuchacz, który
            // odpaliłby pobranie rezultatu.


            Method getResultMethod = p.getDeclaredMethod("getResult");

            ExecutorService executor = Executors.newSingleThreadExecutor();

            // uruchom zadanie, które skończy się, gdy result!=null
            executor.submit(() -> {
                String result = null;
                while (true) {
                    // System.out.println(scheduleFuture.isDone());

                    try {
                        Thread.sleep(800);

                        // String result = (String) getResultMethod.invoke(o,new Object[] {});
                        result = (String) getResultMethod.invoke(o);
                    } catch (InterruptedException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    if (result != null) {
                        System.out.println("Result: " + result);
                        break;
                    }
                }
                executor.shutdown();
            });

            System.out.println("main FINISHED");

        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
                 | IllegalArgumentException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
