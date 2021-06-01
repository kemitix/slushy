package net.kemitix.slushy.app;

public interface RetryConfig {

    /**
     * How often, in seconds, to scan the list.
     */
    long getScanPeriod();

    /**
     * The initial redelivery delay in milliseconds.
     */
    long getRetryDelay();

    /**
     * The maximum redeliveries.
     *
     * <p>Derived from the scan period and retry delay, so that retries will
     * occur until the next scan period starts.</p>
     */
    default int getMaxRetries() {
        return (int) Math.floorDiv(getScanPeriod(), getRetryDelay());
    }

}
