package kacper.lab9;


import kacper.lab9.graph.Actor;
import kacper.lab9.graph.Movie;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.graph.SimpleGraph;
import org.json.JSONArray;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GraphController {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    private final ActorLinkLookupService actorLinkLookupService;
    private final RestTemplate restGraphTemplate;


    private static final Logger logger = LoggerFactory.getLogger(GraphController.class);

    public GraphController(ActorLinkLookupService actorLinkLookupService, RestTemplateBuilder restGraphTemplateBuilder) {
        this.actorLinkLookupService = actorLinkLookupService;
        this.restGraphTemplate = restGraphTemplateBuilder.build();
    }

    public void cprint(String color, String string){
        System.out.println(color + string + ANSI_RESET);
    }

    @Async("webExecutor")
    public CompletableFuture<JSONArray> getRoad(String id1, String id2) throws Exception {
        try{
            long startTimer = System.currentTimeMillis();
            Graph<Actor, Movie> g = new SimpleGraph<>(Movie.class);

            CompletableFuture<Actor> page1 = actorLinkLookupService.findActor(id1);
            CompletableFuture<Actor> page2 = actorLinkLookupService.findActor(id2);
            CompletableFuture.allOf(page1,page2).join();
            List<CompletableFuture<Movie>> cfMovieList = new ArrayList<>();


            Actor startActor = page1.get();
            Actor targetActor = page2.get();
            Queue<Actor> actorsQueue = new ArrayDeque<>();
            actorsQueue.add(startActor);
            g.addVertex(startActor);

            boolean targetFound = false;

            while (!actorsQueue.isEmpty()){
                Actor sourceActor = actorsQueue.remove();
                CompletableFuture<List<Movie>> movieListPage = actorLinkLookupService.findMovieList(sourceActor.getId());
                CompletableFuture.allOf(movieListPage).join();
                sourceActor.setMovies(movieListPage.get());
                List<Movie> movieList = sourceActor.getMovies();
                System.out.println(ANSI_YELLOW + "Searching for " + sourceActor.getName() + " movies' actors" + ANSI_RESET);
                for (int i = 0; i < movieList.size(); i++){
/*               CompletableFuture<Movie> moviePage = actorLinkLookupService.findMovie(movieList.get(i).getId());
               CompletableFuture.allOf(moviePage).join();
               movieList.set(i, moviePage.get());*/
                    cfMovieList.add(actorLinkLookupService.findMovie(movieList.get(i).getId()));
                    //if (i%20 == 0) System.out.print("M" + i + " ");
                }
                CompletableFuture.allOf(cfMovieList.toArray(new CompletableFuture<?>[cfMovieList.size()])).join();
                //cprint(ANSI_YELLOW, sourceActor.getName() + "'s movies stop");
                for (int i = 0; i < movieList.size(); i++){
                    movieList.set(i, cfMovieList.get(i).get());
                }
                //System.out.print("\n");
                for (int i = 0; i < sourceActor.getMovies().size(); i++){
                    List<Actor> actorList = sourceActor.getMovies().get(i).getActors();
                    if (actorList.contains(targetActor)){
                        targetFound = true;
                        g.addVertex(targetActor);
                        g.addEdge(sourceActor,targetActor,(Movie) sourceActor.getMovies().get(i).clone());
                        cprint(ANSI_PURPLE,"TARGET FOUND");
                        break;
                    }
                    for (int j = 0; j < actorList.size(); j++){
                        Actor actor = actorList.get(j);
                        if (actor.equals(sourceActor)) continue;
                        if (!g.containsVertex(actor)){
                            actorsQueue.add(actor);
                            g.addVertex(actor);
                            g.addEdge(sourceActor, actor, (Movie) sourceActor.getMovies().get(i).clone());
                        } else if (!g.containsEdge(sourceActor,actor)){
                            g.addEdge(sourceActor, actor, (Movie) sourceActor.getMovies().get(i).clone());
                        }
                    }
                }
                if (targetFound == true) break;
            }
            cprint(ANSI_PURPLE, "SEARCHING FOR SHORTEST PATH");
            Set<Actor> vertices = g.vertexSet();
            BellmanFordShortestPath<Actor, Movie> bfsp = new BellmanFordShortestPath<>(g);
            GraphPath<Actor, Movie> shortestPath = bfsp.getPath(startActor, targetActor);
            List<Movie> graphEdges = shortestPath.getEdgeList();
            List<Actor> sPathActors = shortestPath.getVertexList();
            for(int i = 0; i < sPathActors.size(); ++i){
                if(i == sPathActors.size()-1)
                    System.out.print(sPathActors.get(i));
                else
                    System.out.print(sPathActors.get(i) + " -> " + graphEdges.get(i).toString() + " -> ");
            }
            JSONArray jsonArray = new JSONArray();
            for(int i = 0; i < sPathActors.size(); ++i){
                if(i == sPathActors.size()-1) {
                    JSONObject jsonObjectVertex = sPathActors.get(i).toJSON();
                    jsonArray.put(jsonObjectVertex);
                }

                else{
                    JSONObject jsonObjectVertex = sPathActors.get(i).toJSON();
                    JSONObject jsonObjectEdge = graphEdges.get(i).toJSON();
                    jsonArray.put(jsonObjectVertex);
                    jsonArray.put(jsonObjectEdge);
                }
            }
            logger.info("\nElapsed time: " + (System.currentTimeMillis() - startTimer + "ms"));
            return CompletableFuture.completedFuture(jsonArray);


        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }


}
