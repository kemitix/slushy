package net.kemitix.slushy.spi;

public interface InboxConfig {
    String getScanPeriod();

    String getRoutingSlip();

    String getListName();
}
