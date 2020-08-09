package net.kemitix.slushy.app;

import lombok.extern.java.Log;
import org.apache.camel.Header;

import javax.enterprise.context.ApplicationScoped;

@Log
@ApplicationScoped
public class LoadList {

    public static final String LIST_NAME = "Slushy.LoadList.Name";

    InboxContext load(InboxContext context, @Header(LIST_NAME) String listName) {
        log.info("Load list: " + listName);
        return context;
    }

}
