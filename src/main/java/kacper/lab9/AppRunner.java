/*
package kacper.lab9;

import kacper.lab9.graph.Actor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AppRunner implements CommandLineRunner {


    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    private final GraphController graphController;

    public AppRunner(GraphController graphController) {
        this.graphController = graphController;
    }

    @Override
    public void run(String... args) throws Exception {
        // Start the clock
        long start = System.currentTimeMillis();

        graphController.getRoad("nm0511277","nm0000206");
        logger.info("Apprunner Elapsed time: " + (System.currentTimeMillis() - start));

    }

}
*/
