package acc.com.geolearning_app.dto;

import java.util.ArrayList;

public class User {

    private String id;
    private String userName;
    private String password;
    private String email;
    private ArrayList<Zone> maps = new ArrayList<>();

    public User(String id, String userName, String email, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Zone> getMaps() {
        return maps;
    }

    public void setMaps(ArrayList<Zone> maps) {
        this.maps = maps;
    }

    public void addMap(Zone map){
        this.maps.add(map);
    }

    public void deleteMap(Zone map){
        this.maps.remove(map);
    }
}
