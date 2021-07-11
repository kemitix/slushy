package net.kemitix.slushy.app;

public interface DueDaysProperties {

    String DUE_DAYS = "due-days";

    /**
     * How long before a still-waiting-to-be-read response should be sent.
     */
    long dueDays();

}
