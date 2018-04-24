package acc.com.geolearning_app.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Zone {

    private String id;
    private Double lat;
    private Double lon;
    private ArrayList<Place> places = new ArrayList<>();
    //-- CONTENT ++
    public static final List<Zone> ZONES = new ArrayList<Zone>();

    public static final Map<String, Zone> ZONE_MAP = new HashMap<String, Zone>();

    private static void addItem(Zone item) {
        ZONES.add(item);
        ZONE_MAP.put(item.getId(), item);
    }

    //TODO mostrar los Places de cada Zone
    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    private String content;
    private String details;

    public Zone(String id, String content, String details) {
        this.id = id;
        this.content = content;
        this.details = details;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return content;
    }

    // CONTENT --


    public Zone() {
    }

    public Zone(String id, Double lat, Double lon) {
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return this.lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
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
