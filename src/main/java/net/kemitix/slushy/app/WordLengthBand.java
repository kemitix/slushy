package net.kemitix.slushy.app;

import lombok.Getter;

import java.util.Arrays;

public enum WordLengthBand {
    LENGTH_VERY_SHORT("very-short", "Very Short"),
    LENGTH_SHORT_SHORT("short-short", "Short Short"),
    LENGTH_LONG_SHORT("long-short", "Long Short"),
    LENGTH_NOVELETTE("novelette", "Novelette"),// legacy
    LENGTH_SHORT_NOVELETTE("short-novelette", "Short Novelette"),
    LENGTH_LONG_NOVELETTE("long-novelette", "Long Novelette")
    ;

    private final String tag;
    @Getter
    private final String value;

    WordLengthBand(String tag, String value) {
        this.tag = tag;
        this.value = value;
    }

    public static WordLengthBand parse(String wordcount) {
        var wc = wordcount.toLowerCase();
        return Arrays.stream(WordLengthBand.values())
                .filter(b -> b.tag.equals(wc))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format(
                                "Invalid band: [%s]", wordcount)));
    }

}
