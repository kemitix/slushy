package net.kemitix.slushy.app.email;

public interface SubjectCreator<T> {

    String subject(T source);

}
