package net.kemitix.slushy.app;

import java.util.Arrays;

public enum WordLengthBand {
    LENGTH_SHORT_SHORT("short-short"),
    LENGTH_LONG_SHORT("long-short"),
    LENGTH_NOVELETTE("novelette"),
    ;

    private final String value;

    WordLengthBand(String value) {
        this.value = value;
    }

    public static WordLengthBand parse(String wordcount) {
        var wc = wordcount.toLowerCase();
        return Arrays.stream(WordLengthBand.values())
                .filter(b -> b.value.equals(wc))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format(
                                "Invalid band: [%s]", wordcount)));
    }
}
