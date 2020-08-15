package net.kemitix.slushy.app;

public interface BodyCreator<T> {

    String bodyText(T source);

    String bodyHtml(T source);

}
