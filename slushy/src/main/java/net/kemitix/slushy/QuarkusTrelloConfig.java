package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.spi.SlushyTrelloConfig;

@Setter
@Getter
@ConfigProperties(prefix = "slushy.trello")
public class QuarkusTrelloConfig
        implements SlushyTrelloConfig {

    String userName;
    String boardName;

}
