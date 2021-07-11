package net.kemitix.slushy.app;

public interface ListProcessProperties
        extends RetryProperties {

    String SOURCE_LIST = "source-list";
    String TARGET_LIST = "target-list";
    String ROUTING_SLIP = "routing-slip";
    String REQUIRED_AGE_HOURS = "required-age-hours";

    /**
     * The name of the list to scan.
     */
    String sourceList();

    /**
     * The name of the list to move cards to once processed.
     */
    String targetList();

    /**
     * The Route to process cards from the list.
     */
    String routingSlip();

    /**
     * How many hours a card must wait in a list without activity
     * before being processed.
     */
    int requiredAgeHours();


}
