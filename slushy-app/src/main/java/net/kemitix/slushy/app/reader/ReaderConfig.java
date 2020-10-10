package net.kemitix.slushy.app.reader;

import net.kemitix.slushy.app.ListProcessConfig;

public interface ReaderConfig extends ListProcessConfig {

    /**
     * The maximum number of cards to place in the list.
     *
     * <p>A value of -1 means there is no limit.</p>
     */
    int getMaxSize();
}
