package net.kemitix.slushy.app.config;

import com.amazonaws.util.StringInputStream;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kemitix.trello.TrelloBoard;
import net.kemitix.trello.TrelloCard;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

@Log
@ApplicationScoped
public class LoadDynamicConfig {

    private final DynamicProperties dynamicProperties =
            DynamicProperties.INSTANCE;

    @Inject DynamicConfigConfig config;
    @Inject TrelloBoard trelloBoard;
    @Inject Instance<DynamicConfig> dynamicConfigs;

    public void load() {
        findConfigCard()
                .map(TrelloCard::getDesc)
                .ifPresent(this::parseConfigCardDesc);
    }

    private Optional<TrelloCard> findConfigCard() {
        return Optional.ofNullable(config.getListName())
                .map(trelloBoard::getListCards)
                .stream()
                .flatMap(Collection::stream)
                .filter(card -> config.getCardName().equals(card.getName()))
                .findFirst();
    }

    @SneakyThrows
    private void parseConfigCardDesc(String desc) {
        StringInputStream inStream = new StringInputStream(desc);
        Properties properties = dynamicProperties.getProperties();
        properties.clear();
        properties.load(inStream);
        log.info("Loaded properties: " + properties);
    }

}
