package net.kemitix.slushy;

import io.quarkus.arc.config.ConfigProperties;
import lombok.Getter;
import lombok.Setter;
import net.kemitix.slushy.app.zeroattachment.ZeroAttachmentConfig;

@Setter
@Getter
@ConfigProperties(prefix = "slushy.zeroattachment")
public class QuarkusZeroAttachmentConfig
        implements ZeroAttachmentConfig {

    String routingSlip;

}
