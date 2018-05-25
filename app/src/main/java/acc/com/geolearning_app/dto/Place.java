package acc.com.geolearning_app.dto;

import java.util.ArrayList;

public class Place {

    private String id;
    private Integer x;
    private Integer y;
    private Integer w;
    private Integer h;
    private String place_type;
    private Double prob;
    private Zone id_map;
    private ArrayList<Tag> tags = new ArrayList<>();
    private ArrayList<Nodo> nodos = new ArrayList<>();



    public Place(Integer x, Integer y, Integer w, Integer h, String place_type, Zone id_map, Double prob) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.place_type = place_type;
        this.id_map = id_map;
        this.prob = prob;
    }

    public Place(){

    }


    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getX() {
        return this.x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return this.y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getW() {
        return this.w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public Integer getH() {
        return this.h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public String getPlace_type() {
        return this.place_type;
    }

    public void setPlace_type(String place_type) {
        this.place_type = place_type;
    }

    public Zone getId_map() {
        return this.id_map;
    }

    public void setId_map(Zone id_map) {
        this.id_map = id_map;
    }

    public Double getProb() {
        return prob;
    }

    public void setProb(Double prob) {
        this.prob = prob;
    }

    public ArrayList<Tag> getTags() {
        return tags;
    }

    public void setTags(ArrayList<Tag> tags) {
        this.tags = tags;
    }

    public void addTag(Tag tag){
        this.tags.add(tag);
    }

    public void deleteTag(Tag tag){
        this.tags.remove(tag);
    }


    public ArrayList<Nodo> getNodos() {
        return nodos;
    }

    public void setNodos(ArrayList<Nodo> nodos) {
        this.nodos = nodos;
    }

    public void addNodo(Nodo nodo){
        this.nodos.add(nodo);
    }

    public void deleteNodo(Nodo nodo){
        this.nodos.remove(nodo);
    }
}
