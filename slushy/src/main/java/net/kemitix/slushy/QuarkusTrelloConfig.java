package net.kemitix.slushy;

import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.spi.SlushyTrelloConfig;

import javax.enterprise.context.ApplicationScoped;

@Setter
@Getter
@ApplicationScoped
public class QuarkusTrelloConfig
        implements SlushyTrelloConfig {

    String userName = System.getenv("SLUSHY_USER");
    String boardName = System.getenv("SLUSHY_BOARD");

}
