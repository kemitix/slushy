package net.kemitix.slushy.app;

import lombok.Getter;

import java.util.Arrays;

public enum WordLengthBand {
    LENGTH_SHORT_SHORT("short-short", "Short"),
    LENGTH_LONG_SHORT("long-short", "Long"),
    LENGTH_NOVELETTE("novelette", "Novelette"),
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
