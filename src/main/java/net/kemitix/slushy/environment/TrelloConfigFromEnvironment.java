package net.kemitix.slushy.environment;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import net.kemitix.slushy.app.RetryProperties;
import net.kemitix.trello.TrelloConfig;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static net.kemitix.slushy.environment.EnvironmentUtil.requiredEnvironment;

@Log
@Setter
@Getter
@ApplicationScoped
public class TrelloConfigFromEnvironment
        implements TrelloConfig {

    private String trelloKey = requiredEnvironment("TRELLO_KEY");
    private String trelloSecret = requiredEnvironment("TRELLO_SECRET");
    private String userName = requiredEnvironment("SLUSHY_USER");
    private String boardName = requiredEnvironment("SLUSHY_BOARD");
    private String sender = requiredEnvironment("SLUSHY_SENDER");
    private String reader = requiredEnvironment("SLUSHY_READER");
    private String webhook = requiredEnvironment("SLUSHY_WEBHOOK");

    @Inject Instance<RetryProperties> retryConfigs;

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
                        timeAsEnglish(config.scanPeriod()),
                        config.maxRetries(), timeAsEnglish(config.retryDelay()))));
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
