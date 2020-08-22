package net.kemitix.slushy.app;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListUtils {

    public static <A, B> List<B> map(List<A> a, Function<A, B> f) {
        return a.stream()
                .map(f)
                .collect(Collectors.toList());
    }

}
