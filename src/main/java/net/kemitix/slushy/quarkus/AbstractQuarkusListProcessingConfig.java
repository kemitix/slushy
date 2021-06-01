package net.kemitix.slushy.quarkus;

import lombok.Setter;
import net.kemitix.slushy.app.ListProcessConfig;

@Setter
public abstract class AbstractQuarkusListProcessingConfig
        extends AbstractQuarkusRetryConfig
        implements ListProcessConfig {

    private String sourceList;
    private String targetList;
    private String routingSlip;
    private int requiredAgeHours;

    @Override
    public String getSourceList() {
        return findValue("source-list")
                .orElse(sourceList);
    }

    @Override
    public String getTargetList() {
        return findValue("target-list")
                .orElse(targetList);
    }

    @Override
    public String getRoutingSlip() {
        return findValue("routing-slip")
                .orElse(routingSlip);
    }

    @Override
    public int getRequiredAgeHours() {
        return findIntegerValue("required-age-hours")
                .orElse(requiredAgeHours);
    }

}
