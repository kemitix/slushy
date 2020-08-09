package net.kemitix.slushy.app;

import lombok.extern.java.Log;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;

@Log
@ApplicationScoped
public class MainRunner {
    public int run(String[] args) {
        log.info("run: " + Arrays.toString(args));
        return 0;
    }
}
