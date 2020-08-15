package net.kemitix.slushy;

import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.spi.SlushyTrelloConfig;

@Setter
@Getter
public class QuarkusTrelloConfig
        implements SlushyTrelloConfig {

    String userName = System.getenv("SLUSHY_USER");
    String boardName = System.getenv("SLUSHY_BOARD");

}
