package kacper.lab9;

import org.hibernate.validator.constraints.URL;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RestController
public class WebController {
    private static final Logger logger = LoggerFactory.getLogger(WebController.class);
    private static int index = 0;
    private final GraphController graphController;

    private List<CompletableFuture<JSONArray>> futureList = new ArrayList<>();

    public WebController(GraphController graphController) {
        this.graphController = graphController;
    }

    @RequestMapping(value = "/actorlink", method = RequestMethod.GET)
    public ResponseEntity startRoadFromId1toId2(@RequestParam("id1") String id1, @RequestParam("id2") String id2) throws Exception {
        int returnIndex = index;
        index++;
        futureList.add(graphController.getRoad(id1,id2));
        //CompletableFuture<JSONArray> thread1 = graphController.getRoad(id1,id2);
        return new ResponseEntity<>(returnIndex, HttpStatus.OK);
    }
    @RequestMapping(value = "/actorlink/{id}/check", method = RequestMethod.GET)
    public ResponseEntity checkRoadFromId1toId2(@PathVariable String id) throws Exception {
        String string = id + " processing";
        if (futureList.get(Integer.parseInt(id)).isDone()){
            string = id + " DONE";
        }
        return new ResponseEntity<>(string, HttpStatus.OK);
    }

/*    @RequestMapping(value = "/actorlink/{id}/stop", method = RequestMethod.GET)
    public ResponseEntity stopRoadFromId1toId2(@PathVariable String id) throws Exception {
        futureList.get(Integer.parseInt(id)).cancel();
        return new ResponseEntity<>("stop", HttpStatus.OK);
    }*/

    @RequestMapping(value = "/actorlink/{id}/result", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity getRoadFromId1toId2(@PathVariable String id) throws Exception {
        if (!futureList.get(Integer.parseInt(id)).isDone()){
            return new ResponseEntity<>("IS NOT DONE", HttpStatus.PROCESSING);
        }
        String jsonString = futureList.get(Integer.parseInt(id)).get().toString();
        return new ResponseEntity<>(jsonString, HttpStatus.OK);
    }


}
