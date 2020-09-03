package net.kemitix.slushy;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class AbstractQuarkusListProcessingConfig {

    String scanPeriod;
    String sourceList;
    String targetList;
    String routingSlip;
    int requiredAgeHours;

}
