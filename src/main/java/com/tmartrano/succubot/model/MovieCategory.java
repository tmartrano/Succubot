package com.tmartrano.succubot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum MovieCategory {
    BAD_MOVIE("Bad"),
    GOOD_MOVIE("Good");

    private static final Map<String, MovieCategory> movieCategoryMap =
            Stream.of(values()).collect(Collectors.toMap(MovieCategory::getValue, Function.identity()));

    private final String name;

    MovieCategory(final String name) {
        this.name = name;
    }

    public String getValue() {
        return name;
    }

    public static MovieCategory forValue(final String search) {
        for (Map.Entry<String, MovieCategory> enumEntry : movieCategoryMap.entrySet()) {
            if (enumEntry.getKey().equalsIgnoreCase(search))
                return enumEntry.getValue();
        }
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<String> getKeys() {
        Set<String> keySet = movieCategoryMap.keySet();
        return new ArrayList<>(keySet);
    }
}
