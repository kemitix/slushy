package net.kemitix.slushy.app.fileconversion;

import fr.opensagres.xdocreport.xhtml.extension.StringEscapeUtils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class HtmlCleaner {

    String clean(String in) {
        return StringEscapeUtils.escapeHtml(in);
    }

}
