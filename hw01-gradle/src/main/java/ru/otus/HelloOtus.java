package ru.otus;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.IntStream;

public class HelloOtus {
    public static void main(String[] args) {
        List<Integer> arr = IntStream.rangeClosed(1,10)
                                     .boxed()
                                     .toList();
        System.out.println(Lists.reverse(arr));
    }
}