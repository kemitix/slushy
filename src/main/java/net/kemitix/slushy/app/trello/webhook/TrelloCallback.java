package net.kemitix.slushy.app.trello.webhook;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TrelloCallback {

    private final String description;
    private final String callbackURL;
    private final String idModel;

}
