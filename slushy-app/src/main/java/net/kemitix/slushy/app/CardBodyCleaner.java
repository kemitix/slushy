package net.kemitix.slushy.app;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CardBodyCleaner {
    public String clean(String in) {
        return in
                .replace("\\", "")
                ;
    }
}
