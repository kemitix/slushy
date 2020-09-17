package net.kemitix.slushy.app.fileconversion;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HtmlCleaner {

    String clean(String in) {
        return in
                .replace("“", "&OpenCurlyDoubleQuote;")
                .replace("”", "&CloseCurlyDoubleQuote;")
                .replace("’", "&CloseCurlyQuote;")
                .replace("—", "&mdash;")
                .replace("–", "&ndash;")
                ;
    }

}
