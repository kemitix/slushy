package net.kemitix.slushy.environment;

import java.util.Objects;

public class EnvironmentUtil {

    public static String requiredEnvironment(String key) {
        return Objects.requireNonNull(System.getenv(key),
                String.format("Required environment variable %s is not set", key));
    }

}
