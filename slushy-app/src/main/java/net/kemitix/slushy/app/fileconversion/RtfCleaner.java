package net.kemitix.slushy.app.fileconversion;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

@ApplicationScoped
public class RtfCleaner {

    public String clean(String rtfString) {
        var text = new AtomicReference<>(rtfString);
        Arrays.stream(new String[][]{
                {"[{][\\\\]u8220[\\\\]'93[}]", "&ldquo;"},
                {"[{][\\\\]u8221[\\\\]'94[}]", "&rdquo;"},
                {"[{][\\\\]u8217[\\\\]'92[}]", "&apos;"},
        }).forEach(e ->
                text.updateAndGet(x ->
                        x.replaceAll(e[0], e[1])));
        return text.get();
    }

}
