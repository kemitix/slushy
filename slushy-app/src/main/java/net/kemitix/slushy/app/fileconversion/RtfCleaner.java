package net.kemitix.slushy.app.fileconversion;

import org.apache.commons.text.StringEscapeUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.regex.Pattern;

@ApplicationScoped
public class RtfCleaner {

    private static final Pattern pattern = Pattern.compile("\\\\u(\\d{4})\\\\'\\d{2}");

    public String clean(String rtfText) {
        return pattern.matcher(rtfText)
                .replaceAll(match -> {
                    int codePoint = Integer.parseInt(match.group(1));
                    String unicodeString = new String(Character.toChars(codePoint));
                    return StringEscapeUtils.escapeHtml4(unicodeString);
                });
    }

}
