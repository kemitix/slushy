package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import net.kemitix.slushy.app.withdraw.WithdrawConfig;

@ConfigProperties(prefix = "slushy.withdraw")
public class QuarkusWithdrawConfig
        extends AbstractQuarkusListProcessingConfig
        implements WithdrawConfig {

}
