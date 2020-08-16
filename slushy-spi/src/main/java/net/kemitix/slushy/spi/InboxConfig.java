package net.kemitix.slushy.spi;

public interface InboxConfig {

    /**
     * How often, in seconds, to scan the inbox list.
     */
    String getScanPeriod();

    /**
     * The Route to process cards from the Inbox with.
     */
    String getRoutingSlip();

    /**
     * The name of the Inbox list.
     */
    String getListName();

    /**
     * How long before a still-waiting-to-be-read response should be sent.
     */
    long getDueDays();

    /**
     * The name of the Slush list.
     */
    String getSlushName();

}
