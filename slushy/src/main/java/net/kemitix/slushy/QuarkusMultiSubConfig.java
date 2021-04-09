package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Setter;
import net.kemitix.slushy.app.config.AbstractDynamicConfig;
import net.kemitix.slushy.app.multisub.MultiSubConfig;

@Setter
@ConfigProperties(prefix = QuarkusMultiSubConfig.CONFIG_PREFIX)
public class QuarkusMultiSubConfig
        extends AbstractDynamicConfig
        implements MultiSubConfig {

    protected static final String CONFIG_PREFIX = "slushy.multisub";

    String lists;

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }

    @Override
    public String getLists() {
        return findValue("lists")
                .orElse(lists);
    }

}
