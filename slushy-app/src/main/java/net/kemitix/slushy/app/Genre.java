package net.kemitix.slushy.app;

import java.util.Arrays;

public enum Genre {
    ScienceFiction,
    Fantasy,
    ScienceFantasy,
    Unknown,
    ;

    public static Genre parse(String genre) {
        var wc = genre.toLowerCase();
        return Arrays.stream(Genre.values())
                .filter(b -> b.toString().toLowerCase().equals(wc))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid genre: " + genre));
    }
}
