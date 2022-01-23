package net.kemitix.slushy.app.config;

import lombok.SneakyThrows;
import lombok.extern.java.Log;
import net.kemitix.slushy.trello.SlushyBoard;
import net.kemitix.trello.TrelloCard;
import software.amazon.awssdk.utils.StringInputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

@Log
@ApplicationScoped
public class LoadDynamicConfig {

    private final DynamicProperties dynamicProperties =
            DynamicProperties.INSTANCE;

    @Inject
    DynamicConfigProperties config;
    @Inject
    SlushyBoard slushyBoard;

    public void load() {
        findConfigCard()
                .map(TrelloCard::getDesc)
                .ifPresent(this::parseConfigCardDesc);
    }

    private Optional<TrelloCard> findConfigCard() {
        return Optional.ofNullable(config.listName())
                .map(slushyBoard::getListCards)
                .stream()
                .flatMap(Collection::stream)
                .filter(card -> config.cardName().equals(card.getName()))
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
