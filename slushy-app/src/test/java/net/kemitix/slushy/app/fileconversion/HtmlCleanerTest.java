package net.kemitix.slushy.app.fileconversion;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

public class HtmlCleanerTest
        implements WithAssertions {

    @Test
    public void encodeLegacyTranslations() {
        //given
        String in = "“”’—–";
        //when
        String out = new HtmlCleaner().clean(in);
        //then
        assertThat(out)
                .isEqualTo("&ldquo;" +
                        "&rdquo;" +
                        "&rsquo;" +
                        "&mdash;" +
                        "&ndash;");
    }
}