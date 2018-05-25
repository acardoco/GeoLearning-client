package acc.com.geolearning_app.dto;

public class Nodo {

    private String id;
    private Double lat;
    private Double lon;
    private Integer type; // 0->x, 1->w, 2->h,3->x2
    Place id_place;

    public Nodo() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Place getId_place() {
        return id_place;
    }

    public void setId_place(Place id_place) {
        this.id_place = id_place;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
