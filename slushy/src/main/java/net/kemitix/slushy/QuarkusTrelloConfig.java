package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.spi.TrelloConfig;

@Setter
@Getter
@ConfigProperties(prefix = "slushy.trello")
public class QuarkusTrelloConfig
        implements TrelloConfig {

    String userName;
    String boardName;

}
