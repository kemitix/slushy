package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.ListProcessProperties;

public interface ReaderProperties
        extends ListProcessProperties {

    String MAX_SIZE = "max-size";

    /**
     * The maximum number of cards to place in the list.
     *
     * <p>A value of -1 means there is no limit.</p>
     */
    int maxSize();
}
