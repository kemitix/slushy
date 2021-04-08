package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.config.AbstractDynamicConfig;
import net.kemitix.slushy.app.multisub.MultiSubConfig;

import java.util.Properties;

@Setter
@Getter
@ConfigProperties(prefix = QuarkusMultiSubConfig.CONFIG_PREFIX)
public class QuarkusMultiSubConfig
        extends AbstractDynamicConfig
        implements MultiSubConfig {

    protected static final String CONFIG_PREFIX = "slushy.multisub";

    String lists;

    @Override
    public void update(Properties properties) {
        super.update(properties);
        update("lists", this::setLists, properties);
    }

    @Override
    public String getConfigPrefix() {
        return CONFIG_PREFIX;
    }
}
