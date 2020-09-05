package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.withdraw.WithdrawConfig;

@Setter
@Getter
@ConfigProperties(prefix = "slushy.withdraw")
public class QuarkusWithdrawConfig
        extends AbstractQuarkusListProcessingConfig
        implements WithdrawConfig {

}
