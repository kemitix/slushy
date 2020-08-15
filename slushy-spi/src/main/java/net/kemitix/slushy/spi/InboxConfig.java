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

    /**
     * The email address to send submission attachments to.
     *
     * <p>e.g. the kindle address</p>
     */
    String getSender();

    /**
     * The email address to send emails from.
     *
     * <p>If sending to Kindle, then ensure this address is listed as a valid sender.</p>
     */
    String getRecipient();

}
