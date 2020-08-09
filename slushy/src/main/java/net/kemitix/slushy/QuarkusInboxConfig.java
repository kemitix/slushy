package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.spi.InboxConfig;

@Setter
@Getter
@ConfigProperties(prefix = "slushy.inbox")
public class QuarkusInboxConfig
        implements InboxConfig {

    private String period;
    private String routingSlip;

}
