package net.kemitix.slushy.app;

import lombok.Getter;

import java.util.Arrays;

public enum Genre {
    ScienceFiction("Science Fiction"),
    Fantasy("Fantasy"),
    ScienceFantasy("Science Fantasy"),
    Unknown("Unknown"),
    ;

    @Getter
    private final String value;

    Genre(String value) {
        this.value = value;
    }

    public static Genre parse(String genre) {
        var wc = genre.toLowerCase();
        return Arrays.stream(Genre.values())
                .filter(b -> b.toString().toLowerCase().equals(wc))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid genre: " + genre));
    }
}
