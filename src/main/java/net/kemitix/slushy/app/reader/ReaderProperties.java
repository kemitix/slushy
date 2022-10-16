package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.ListProcessProperties;

public interface ReaderProperties
        extends ListProcessProperties {

    String MAX_SIZE = "max-size";
    String READER = "reader";

    /**
     * The maximum number of cards to place in the list.
     *
     * <p>A value of -1 means there is no limit.</p>
     */
    int maxSize();

    /**
     * The email address of the Reader to receive documents from the Slush list.
     */
    String reader();
}
