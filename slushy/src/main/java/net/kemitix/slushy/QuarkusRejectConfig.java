package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.spi.RejectConfig;


@Setter
@Getter
@ConfigProperties(prefix = "slushy.reject")
public class QuarkusRejectConfig
        implements RejectConfig {

    String scanPeriod;
    String rejectName;
    String rejectedName;
    String routingSlip;
    long requiredAgeHours;

}
