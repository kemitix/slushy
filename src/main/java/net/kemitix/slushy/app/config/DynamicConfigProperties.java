package net.kemitix.slushy.app.config;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DynamicConfigProperties
        implements ConfigProperties {

    public String listName() {return System.getenv("SLUSHY_CONFIG_LIST");}
    public String cardName() {return System.getenv("SLUSHY_CONFIG_CARD");}

}
