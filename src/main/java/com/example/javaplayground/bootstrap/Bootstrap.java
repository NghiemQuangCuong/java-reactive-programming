package com.example.javaplayground.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by cuongnghiem on 01/11/2021
 **/
@Component
public class Bootstrap implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // streamCreation();
        // streamParallel();
        // streamMap();
        // streamFlatMap()


    }

    private void streamCreation(){
        String[] arr = new String[]{"a", "b", "c", "a"};
        Stream<String> stream = Arrays.stream(arr);
        List<String> list = stream.filter(c -> c.equals("a")).collect(Collectors.toList());
        list.forEach(System.out::println);
    }

    private void streamParallel(){
        Integer[] integers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        Stream<Integer> stream = Arrays.stream(integers);
        stream.parallel().forEach(System.out::println);
    }

    public void streamMap(){
        String[] fruits = new String[]{"Apple", "Lemon", "Banana", "Dragon Fruit"};
        Stream<Integer> stream = Arrays.stream(fruits).map(String::length);
        stream.forEach(System.out::println);
    }

    public void streamFlatMap(){
        List<List<Integer>> numbers = new ArrayList<>();
        numbers.add(Arrays.asList(1, 2, 3));
        numbers.add(Arrays.asList(4));
        numbers.add(Arrays.asList(5, 6, 7, 8));
        numbers.add(Arrays.asList(9, 10, 11));
        numbers.add(Arrays.asList(12, 13));
        List<Integer> list = numbers.stream().flatMap(a -> a.stream()).collect(Collectors.toList());
        list.forEach(System.out::println);
    }
}
