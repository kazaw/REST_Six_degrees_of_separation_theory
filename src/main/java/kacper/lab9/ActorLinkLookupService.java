package kacper.lab9;

import kacper.lab9.graph.Actor;
import kacper.lab9.graph.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
@Service
public class ActorLinkLookupService {

    private final static Logger logger = LoggerFactory.getLogger(ActorLinkLookupService.class);

    private final RestTemplate restTemplate;

    public ActorLinkLookupService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async("linkLookupExecutor")
    public CompletableFuture<Actor> findActor(String id) throws InterruptedException {
        logger.info("Looking up actor  " + id);
        String url = String.format("https://java.kisim.eu.org/actors/%s", id);
        Actor results = restTemplate.getForObject(url, Actor.class);
        return CompletableFuture.completedFuture(results);
    }

    @Async("linkLookupExecutor")
    public CompletableFuture<Movie> findMovie(String id) throws InterruptedException {
        //logger.info("Looking up movie  " + id);
        //System.out.print("x");
        String url = String.format("https://java.kisim.eu.org/movies/%s", id);
        Movie results = restTemplate.getForObject(url, Movie.class);
        return CompletableFuture.completedFuture(results);
    }

    @Async("linkLookupExecutor")
    public CompletableFuture<List<Movie>> findMovieList(String id) throws InterruptedException {
        //logger.info("Looking up movie list  " + id);
        String url = String.format("https://java.kisim.eu.org/actors/%s/movies", id);
        Movie[] tmp = restTemplate.getForObject(url, Movie[].class);
        List<Movie> results = new ArrayList<Movie>(Arrays.asList(tmp));
        return CompletableFuture.completedFuture(results);
    }
}

