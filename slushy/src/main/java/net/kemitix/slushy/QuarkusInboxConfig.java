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

    private String scanPeriod;
    private String routingSlip;
    private String listName;
    private long dueDays;
    private String slushName;
    private String sender = System.getenv("SLUSHY_SENDER");
    private String reader = System.getenv("SLUSHY_READER");

}
