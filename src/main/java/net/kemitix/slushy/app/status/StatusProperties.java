package net.kemitix.slushy.app.status;

public interface StatusProperties {
    String LOG_PERIOD = "log-period";
    /**
     * How often to report the status in seconds.
     */
    int logPeriod();

    default String listName() {
        return System.getenv("SLUSHY_STATUS_LIST");
    }

    default String cardName() {
        return System.getenv("SLUSHY_STATUS_CARD");
    }


}
