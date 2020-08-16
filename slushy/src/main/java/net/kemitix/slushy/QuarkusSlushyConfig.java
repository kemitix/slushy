package net.kemitix.slushy;

import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.spi.SlushyConfig;

import javax.enterprise.context.ApplicationScoped;

@Setter
@Getter
@ApplicationScoped
public class QuarkusSlushyConfig
        implements SlushyConfig {

    private String trelloKey = System.getenv("TRELLO_KEY");
    private String trelloSecret = System.getenv("TRELLO_SECRET");
    private String userName = System.getenv("SLUSHY_USER");
    private String boardName = System.getenv("SLUSHY_BOARD");
    private String sender = System.getenv("SLUSHY_SENDER");
    private String reader = System.getenv("SLUSHY_READER");

}
