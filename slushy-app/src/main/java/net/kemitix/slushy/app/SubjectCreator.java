package net.kemitix.slushy.app;

public interface SubjectCreator<T> {

    String subject(T source);

}
