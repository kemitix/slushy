package net.kemitix.slushy.app.status;

public interface StatusConfig {

    /**
     * How often to report the status in seconds.
     */
    int getLogPeriod();

    /**
     * The name of the List to report the status to.
     */
    String getListName();

    /**
     * The name of the Card to report the status to.
     */
    String getCardName();

}
