package net.kemitix.slushy.app.email;

public interface BodyCreator<T> {

    String bodyText(T source);

    String bodyHtml(T source);

}
