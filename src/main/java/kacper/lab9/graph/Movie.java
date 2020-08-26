package kacper.lab9.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jgrapht.graph.DefaultEdge;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//@JsonIgnoreProperties(ignoreUnknown=true)
public class Movie extends DefaultEdge {
    private String title;
    private String id;
    private List<Actor> actors = new ArrayList();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    @Override
    public String toString() {
        return title + " (" + id + ")";
    }

    public Movie() {
        super();
    }

    public Movie(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public JSONObject toJSON() {

        JSONObject jo = new JSONObject();
        jo.put("ID", id);
        jo.put("Title", title);

        return jo;
    }
}

