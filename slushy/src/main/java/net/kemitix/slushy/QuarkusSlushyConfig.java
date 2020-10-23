package net.kemitix.slushy;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.RetryConfig;
import net.kemitix.slushy.app.SlushyConfig;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

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
    private String webhook = System.getenv("SLUSHY_WEBHOOK");

    @Inject Instance<RetryConfig> retryConfigs;

    @PostConstruct
    void init() {
        log.info(String.format("TRELLO_KEY: %s", trelloKey));
        log.info(String.format("TRELLO_SECRET: %b", trelloSecret.length() > 0));
        log.info(String.format("SLUSHY_USER: %s", userName));
        log.info(String.format("SLUSHY_BOARD: %s", boardName));
        log.info(String.format("SLUSHY_SENDER: %s", sender));
        log.info(String.format("SLUSHY_READER: %s", reader));
        log.info(String.format("SLUSHY_WEBHOOK: %s", webhook));
        retryConfigs.forEach(config ->
                log.info(String.format(
                        "[%s] scan every %s; retry: %d times, every %s",
                        config.getClass().getSimpleName(),
                        timeAsEnglish(config.getScanPeriod()),
                        config.getMaxRetries(), timeAsEnglish(config.getRetryDelay()))));
    }

    private String timeAsEnglish(long millis) {
        long seconds = millis / 1000;
        long hours = Math.floorDiv(seconds, 60 * 60);
        long mins = Math.floorDiv(seconds - (hours * 60 * 60), 60);
        long secs = Math.floorMod(seconds, 60);
        List<String> fragments = new ArrayList<>();
        if (hours > 0) {
            fragments.add(String.format("%d hours", hours));
        }
        if (mins > 0) {
            fragments.add(String.format("%d minutes", mins));
        }
        if (secs > 0) {
            fragments.add(String.format("%s seconds", secs));
        }
        return String.join(" ", fragments);
    }

}
