package org.util;

import java.util.Arrays;

public class PrintUtil {

    public static <T> void print(T... args) {
        System.out.println(Arrays.toString(args));
    }
}
