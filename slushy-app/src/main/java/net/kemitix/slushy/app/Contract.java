package net.kemitix.slushy.app;

import java.util.Arrays;

public enum Contract {
    ORIGINAL("original"),
    REPRINT("reprint"),
    ;

    private final String value;

    Contract(String value) {
        this.value = value;
    }

    public static Contract parse(String contract) {
        return Arrays.stream(Contract.values())
                .filter(b -> b.value.equals(contract))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid contract: " + contract));
    }
}
