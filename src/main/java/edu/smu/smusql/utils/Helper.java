package edu.smu.smusql.utils;

import java.util.Arrays;

public final class Helper {
    public static String[] trimQuotes(String[] values) {
        return Arrays.stream(values)
                .map(Helper::trimQuotes)
                .toArray(String[]::new);
    }

    public static String trimQuotes(String value) {
        return value.replaceAll("^'|'$", "");
    }
}
