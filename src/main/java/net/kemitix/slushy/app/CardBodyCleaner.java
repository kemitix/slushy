package net.kemitix.slushy.app;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CardBodyCleaner {
    public String clean(String in) {
        return in
                .replace("\\", "")
                ;
    }
}
