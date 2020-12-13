package net.kemitix.slushy.app.fileconversion;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RtfCleanerTest
    implements WithAssertions {

    @Test
    @DisplayName("Can convert RTF Unicode into an HTML entity")
    public void canConvertRtfUnicodeToHtmlEntity() {
        //given
        var rtf = "\\u8217\\'92";// &apos; or &rsquo;
        RtfCleaner rtfCleaner = new RtfCleaner();
        //when
        String out = rtfCleaner.clean(rtf);
        //then
        assertThat(out).isEqualTo("&rsquo;");
    }
}