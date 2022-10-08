package net.kemitix.slushy.app;

import lombok.Getter;

import java.util.Arrays;

public enum Window {
    GENERAL("general", "General"),
    DIVERSITY("diversity", "Diversity")
    ;

    private final String tag;
    @Getter
    private final String value;

    Window(String tag, String value) {
        this.tag = tag;
        this.value = value;
    }

    public static Window parse(String window) {
        var wc = window.toLowerCase();
        return Arrays.stream(Window.values())
                .filter(b -> b.tag.equals(wc))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(String.format(
                                "Invalid window: [%s]", window)));
    }

}
