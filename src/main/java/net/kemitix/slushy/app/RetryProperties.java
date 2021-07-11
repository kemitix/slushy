package net.kemitix.slushy.app;

public interface RetryProperties {

    String SCAN_PERIOD = "scan-period";
    String RETRY_DELAY = "retry-delay";

    /**
     * How often, in seconds, to scan the list.
     */
    long scanPeriod();


    /**
     * The initial redelivery delay in milliseconds.
     */
    long retryDelay();

    /**
     * The maximum redeliveries.
     *
     * <p>Derived from the scan period and retry delay, so that retries will
     * occur until the next scan period starts.</p>
     */
    default int maxRetries() {
        return (int) Math.floorDiv(scanPeriod(), retryDelay());
    }

}
