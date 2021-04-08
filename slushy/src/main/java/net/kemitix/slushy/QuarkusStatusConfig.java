package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.status.StatusConfig;

@Setter
@Getter
@ConfigProperties(prefix = "slushy.status")
public class QuarkusStatusConfig
        implements StatusConfig {

    private int logPeriod;
    private String listName = System.getenv("SLUSHY_STATUS_LIST");
    private String cardName = System.getenv("SLUSHY_STATUS_CARD");

}
