package net.kemitix.slushy.app.inbox;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = InboxConfigMapping.PREFIX)
public interface InboxConfigMapping
        extends InboxProperties {
    String PREFIX  = "slushy.inbox";
}
