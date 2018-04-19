package acc.com.geolearning_app.dto;

import java.util.ArrayList;

public class Map {

    private String id;
    private float lat;
    private float lon;
    private User id_user;
    private ArrayList<Place> places = new ArrayList<>();

    public Map(String id, float lat, float lon, User id_user) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.id_user = id_user;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getLat() {
        return this.lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return this.lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public User getId_user() {
        return this.id_user;
    }

    public void setId_user(User id_user) {
        this.id_user = id_user;
    }

    public ArrayList<Place> getPlaces() {
        return this.places;
    }

    public void setPlaces(ArrayList<Place> places) {
        this.places = places;
    }

    public void addPlace(Place place){
        this.places.add(place);
    }

    public void deletePlace(Place place){
        this.places.remove(place);
    }
}
