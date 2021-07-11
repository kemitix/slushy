package net.kemitix.slushy.app.status;

public interface StatusProperties {
    String LOG_PERIOD = "log-period";
    /**
     * How often to report the status in seconds.
     */
    int logPeriod();
}
