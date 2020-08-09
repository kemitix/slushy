package net.kemitix.slushy.app;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InboxContext {
    public static InboxContext empty() {
        return new InboxContext();
    }
}
