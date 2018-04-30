package acc.com.geolearning_app.dto;

public class Tag {

    String id;
    String key;
    String value;
    Place id_place;


    public Tag(){ }

    public Tag(String id, String key, String value, Place id_place) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.id_place = id_place;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Place getId_place() {
        return id_place;
    }

    public void setId_place(Place id_place) {
        this.id_place = id_place;
    }
}
