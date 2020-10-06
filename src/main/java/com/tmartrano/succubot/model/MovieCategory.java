package com.tmartrano.succubot.model;

import java.util.Map;
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

    public MovieCategory forValue(String value) {
        return movieCategoryMap.get(value);
    }

    @Override
    public String toString() {
        return name;
    }
}
