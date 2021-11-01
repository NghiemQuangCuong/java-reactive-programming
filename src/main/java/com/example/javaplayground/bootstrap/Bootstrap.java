package com.example.javaplayground.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

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

        Integer[] integers = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
        Stream<Integer> stream = Arrays.stream(integers);
        stream.parallel().forEach(System.out::println);
    }

    private void streamCreation(){
        String[] arr = new String[]{"a", "b", "c", "a"};
        Stream<String> stream = Arrays.stream(arr);
        List<String> list = stream.filter(c -> c.equals("a")).collect(Collectors.toList());
        list.forEach(System.out::println);
    }
}
