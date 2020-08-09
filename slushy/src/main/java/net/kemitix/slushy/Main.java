package net.kemitix.slushy;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import net.kemitix.slushy.app.MainRunner;

import javax.inject.Inject;

@QuarkusMain
public class Main implements QuarkusApplication {

    @Inject
    MainRunner mainRunner;

    @Override
    public int run(String... args) throws Exception {
        return mainRunner.run(args);
    }
}
