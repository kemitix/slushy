package net.kemitix.slushy.app.withdraw;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = WithdrawConfigMapping.PREFIX)
public interface WithdrawConfigMapping
        extends WithdrawProperties {
    String PREFIX = "slushy.withdraw";

}
