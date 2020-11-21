package net.kemitix.slushy.app;

import java.util.Arrays;

public enum WordLengthBand {
    WORDS_1000_3000("3K"),
    WORDS_3001_5000("5K"),
    WORDS_5001_7000("7K"),
    WORDS_7001_9000("9K"),
    WORDS_9001_10000("10K"),
    ;

    private final String value;

    WordLengthBand(String value) {
        this.value = value;
    }

    public static WordLengthBand parse(String wordcount) {
        return Arrays.stream(WordLengthBand.values())
                .filter(b -> b.value.equals(wordcount.toUpperCase()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid band: " + wordcount));
    }
}
