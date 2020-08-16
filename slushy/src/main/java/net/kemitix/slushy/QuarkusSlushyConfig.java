package net.kemitix.slushy;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.kemitix.slushy.spi.SlushyConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@Log
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

    @PostConstruct
    void init() {
        log.info(String.format("TRELLO_KEY: %s", trelloKey));
        log.info(String.format("TRELLO_SECRET: %b", trelloSecret.length() > 0));
        log.info(String.format("SLUSHY_USER: %s", userName));
        log.info(String.format("SLUSHY_BOARD: %s", boardName));
        log.info(String.format("SLUSHY_SENDER: %s", sender));
        log.info(String.format("SLUSHY_READER: %s", reader));
    }

}
