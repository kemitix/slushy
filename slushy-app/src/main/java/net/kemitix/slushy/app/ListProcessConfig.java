package net.kemitix.slushy.app;

public interface ListProcessConfig
        extends RetryConfig {

    /**
     * The name of the list to scan.
     */
    String getSourceList();

    /**
     * The name of the list to move cards to once processed.
     */
    String getTargetList();

    /**
     * The Route to process cards from the list.
     */
    String getRoutingSlip();

    /**
     * How many hours a card must wait in a list without activity
     * before being processed.
     */
    int getRequiredAgeHours();

}
