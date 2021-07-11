package net.kemitix.slushy.app.zeroattachment;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = ZeroAttachmentConfigMapping.PREFIX)
public interface ZeroAttachmentConfigMapping
        extends ZeroAttachmentProperties {
    String PREFIX = "slushy.zeroattachment";
}
