package net.kemitix.slushy.app.email;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = EmailConfigMapping.PREFIX)
public interface EmailConfigMapping
        extends EmailProperties {
    String PREFIX = "slushy.email";
}
