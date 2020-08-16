package net.kemitix.slushy.spi;

public interface HoldConfig {

    /**
     * How often, in seconds, to scan the reject list.
     */
    String getScanPeriod();

    /**
     * The name of the list of submissions to be rejected.
     */
    String getHoldName();

    /**
     * The name of the list to move rejected submissions to.
     */
    String getHeldName();

    /**
     * The Route to process cards from the reject list with.
     */
    String getRoutingSlip();

    /**
     * How many hours a card must wait in reject list without activity
     * before being rejected.
     */
    int getRequiredAgeHours();
}
