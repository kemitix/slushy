package net.kemitix.slushy.app;

public interface ListProcessConfig {

    /**
     * How often, in seconds, to scan the list.
     */
    long getScanPeriod();

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

    /**
     * The maximum redeliveries.
     *
     * <p>Derived from the scan period and retry delay, so that retries will
     * occur until the next scan period starts.</p>
     */
    default int getMaxRetries() {
        return (int) Math.floorDiv(getScanPeriod(), getRetryDelay());
    }

    /**
     * The initial redelivery delay in milliseconds.
     */
    long getRetryDelay();

}
