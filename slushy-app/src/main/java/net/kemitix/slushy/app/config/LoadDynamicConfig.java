package net.kemitix.slushy.app.config;

import com.amazonaws.util.StringInputStream;
import lombok.SneakyThrows;
import net.kemitix.trello.TrelloBoard;
import net.kemitix.trello.TrelloCard;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Collection;
import java.util.Optional;
import java.util.Properties;

@ApplicationScoped
public class LoadDynamicConfig {

    @Inject DynamicConfigConfig config;
    @Inject TrelloBoard trelloBoard;
    @Inject Instance<DynamicConfig> dynamicConfigs;

    public void load() {
        findConfigProperties()
                .ifPresent(properties -> {
                    dynamicConfigs.stream()
                            .forEach(dynamicConfig -> {
                                dynamicConfig.update(properties);
                            });
                });
    }

    private Optional<Properties> findConfigProperties() {
        return findConfigCard()
                .map(TrelloCard::getDesc)
                .map(this::parseConfigCardDesc);
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
    private Properties parseConfigCardDesc(String desc) {
        Properties properties = new Properties();
        StringInputStream inStream = new StringInputStream(desc);
        properties.load(inStream);
        return properties;
    }

}
